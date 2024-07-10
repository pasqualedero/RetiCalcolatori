package aprile23;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.*;
import java.util.Random;

public class Server {
    private RegistroAste registro = new RegistroAste();
    private final static int udpCliente=4000, udpMulticast=5000;
    private final static String MulticastAddress = "230.0.0.1";
    private int porta;
    private int[] aste;
    private final static int TIMEOUT = 20000;

    public Server(int[] aste) {
        if (Math.random()<=0.5)
            porta=30000;
        else porta=40000;
        this.aste=aste;
    }

    public void inizia(){
        try{
            ServerSocket serverSocket = new ServerSocket(porta);
            for (int i=0; i<aste.length; i++){
                registro.addOfferta(new Offerta("",i,0),InetAddress.getLocalHost());
                new Invio(i).start();
            }
            new Ascolto(serverSocket).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class Ascolto extends Thread{
        private ServerSocket s;

        public Ascolto(ServerSocket s) {
            this.s = s;
        }

        @Override
        public void run() {
            try{
                while (true){
                    System.out.println("avviato ascolto");
                    Socket socket = s.accept();
                    System.out.println("Si connette "+socket.getInetAddress());

                    PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                    Offerta offerta = (Offerta) in.readObject();

                    if (!registro.astaAperta(offerta.getIdAsta())){
                        out.write("false");
                        return;
                    }
                    registro.addOfferta(offerta,socket.getInetAddress());
                    out.write("true");

                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    class Invio extends Thread{
        private int idAsta;

        public Invio(int idAsta) {
            this.idAsta = idAsta;
        }

        @Override
        public void run() {
            try {
                sleep(new Random().nextInt(5000));

                System.out.println("avviata asta "+idAsta);

                MulticastSocket ms = new MulticastSocket();
                ms.joinGroup(InetAddress.getByName(MulticastAddress));
                String msg = idAsta + " "+porta+ " "+"prodotto"+idAsta;
                byte[] buf = new byte[512];
                buf = msg.getBytes();
                DatagramPacket packet = new DatagramPacket(buf, buf.length,InetAddress.getByName(MulticastAddress),udpMulticast);
                ms.send(packet);
                new TimeOut(idAsta).start();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class TimeOut extends Thread{
        private int idAsta;

        public TimeOut(int idGara) {
            this.idAsta = idGara;
        }

        @Override
        public void run() {
            try {
                System.out.println("Avviato timeout di asta "+idAsta);
                sleep(TIMEOUT);
                System.out.println("Conclusa asta "+idAsta);
                registro.setApertura(false,idAsta);
                inviaVincitore(idAsta);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        private void inviaVincitore(int idAsta)  {
            try {
                DatagramSocket datagramSocket = new DatagramSocket();
                byte[] buf = new byte[512];
                String msg = "Hai vinto l'asta "+idAsta;
                buf=msg.getBytes();
                DatagramPacket packet = new DatagramPacket(buf, buf.length,registro.getCliente(idAsta), udpCliente);
                datagramSocket.send(packet);
                System.out.println("Esito inviato");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        int[] aste = {3,6,43,45,4};
        Server s = new Server(aste);
        s.inizia();
    }
}
