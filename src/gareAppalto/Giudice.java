package gareAppalto;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.*;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class Giudice {
    // tutte le porte sono STATIC e FINAL
    private static final int rPort=2000; //ricezione da ente
    private static final int gPort=3000; //invio multicast
    private static final String gAddress="230.0.0.1"; //indirizzo multicast
    private static final int nPort=4000; //ricezione connessioni partecipanti
    private static final Semaphore mutex = new Semaphore(1); // gestione HashMap
    private Calendar limite; // 1 minuto
    private LinkedList<Offerta> offerte= new LinkedList<>();
    private InetAddress group;

    public Giudice(Calendar limite){
        try {
            this.group = InetAddress.getByName(gAddress);
            this.limite=limite;
            System.out.println("Server creato");
        }catch (UnknownHostException e){
            System.out.println("Creazione gruppo fallita");
        }
    }

    public Richiesta riceviRichiesta(){
        try {
            // creo canale
            ServerSocket serverSocket = new ServerSocket(rPort);
            System.out.println("Attendo richiesta da ente");
            Socket socket = serverSocket.accept();
            // istanzio lettore
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            return (Richiesta) in.readObject();
        }catch (IOException e){
            System.err.println(e);
        } catch (ClassNotFoundException e) {
            System.err.println(e);
        }
        return null;
    }

    public void inoltraRichiesta(Richiesta richiesta){
        try {
            MulticastSocket multicastSocket = new MulticastSocket(gPort);
            String r = richiesta.getDescr()+" - "+richiesta.getImpMax();
            byte[] buf = new byte[256];
            DatagramPacket packet = new DatagramPacket(buf, buf.length, group,gPort);
            multicastSocket.send(packet);
            System.out.println("Richiesta inoltrata al gruppo");
        }catch (IOException e){
            System.err.println(e);
        }
    }

    public void riceviOfferte(){
        try{
            ServerSocket serverSocket = new ServerSocket(nPort);
            Calendar now = Calendar.getInstance();
            while (true){
                Socket cliente = serverSocket.accept();
                new Recettore(cliente,now).start();
            }

        }catch (IOException e){
            System.err.println(e);
        }
    }

    class Recettore extends Thread{
        private Socket socket;
        private Calendar now;

        public Recettore(Socket socket, Calendar now) {
            this.socket = socket;
            this.now=now;
        }

        @Override
        public void run() {
            try{
                if (now.before(limite)){
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                    PrintWriter out = new PrintWriter(socket.getOutputStream(),true);

                    Offerta offerta = (Offerta) in.readObject();
                    out.println("Offerta ricevuta!");
                    InetAddress idCliente = socket.getInetAddress();

                    mutex.acquire();
                    offerte.add(offerta);
                    mutex.release();
                }
                else{
                    System.out.println("Time elapsed");
                }
            }catch (Exception e){
                System.err.println(e);
            }

        }
    }

    public void determinaVincitore(){
        Offerta vincente = null;
        int max = -1;
        for (Offerta o : offerte){
            if (o.getImporto()>max){
                max=o.getImporto();
                vincente=o;
            }
        }
        try {
            MulticastSocket multicastSocket = new MulticastSocket(gPort);
            byte[] buf = new byte[256];
            String res = vincente.getId()+" - "+vincente.getImporto();
            buf=res.getBytes();
            DatagramPacket packet = new DatagramPacket(buf,buf.length,group,gPort);
            multicastSocket.send(packet);
            System.out.println(vincente.getId());
        } catch (IOException e){
            System.err.println(e);
        }
    }

    public static void main(String[] args) throws Exception{
        // Creo il limite
        Calendar limite = Calendar.getInstance();
        limite.add(Calendar.MINUTE,1);
        Calendar now = Calendar.getInstance();

        Giudice giudice = new Giudice(limite);

        Richiesta r = giudice.riceviRichiesta();
        giudice.inoltraRichiesta(r);
        giudice.riceviOfferte();
        Thread.sleep(limite.getTimeInMillis()-now.getTimeInMillis());
        giudice.determinaVincitore();
    }
}
