package marzo24;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.Semaphore;

public class Server {
    private final static int statoSensPorta=3000, richiestaNotif=4000, udpClient=4000;
    private HashMap<Integer, Stack<StatoSensore>> mappa = new HashMap<>();
    private HashMap<Integer, InetAddress> lista = new HashMap<>();
    private Semaphore mutexMappa = new Semaphore(1);
    private Semaphore mutexLista = new Semaphore(1);

    private static int numProg = 1;

    //costruttore di default

    class Accepter extends Thread{
        private ServerSocket s;
        private boolean b;
        public Accepter(ServerSocket s, boolean b) {
            this.s = s;
            this.b=b;
        }

        @Override
        public void run() {
            try{
                while (true) {
                    if (!b)
                        new HandlerReq(s.accept()).start();
                    else new HandlerReg(s.accept()).start();
                }
            }catch (IOException e){e.printStackTrace();}
        }
    }

    class HandlerReq extends Thread{
        private Socket s;

        public HandlerReq(Socket s) {
            this.s = s;
        }

        @Override
        public void run() {
            try {
                Calendar adesso = Calendar.getInstance();
                int ora = adesso.get(Calendar.HOUR_OF_DAY);

                PrintWriter out = new PrintWriter(s.getOutputStream(),true);
                if (ora < 8 || ora > 13) {
                    out.write("Non puoi mandareeee"); return;
                }

                ObjectInputStream in = new ObjectInputStream(s.getInputStream());
                StatoSensore stato = (StatoSensore) in.readObject();
                if (verifica(stato)){
                    mutexMappa.acquire();
                    numProg++;
                    stato.setNumProg(numProg);
                    if (!mappa.containsKey(stato.getId()))
                        mappa.put(stato.getId(),new Stack<>());
                    mappa.get(stato.getId()).push(stato);
                    inviaIscritti(stato);
                    mutexMappa.release();

                    out.write("numProgr: "+stato.getNumProg());
                } else
                    out.write("Non rispetta le condizioni");
                in.close();out.close();
                s.close();
            } catch (IOException e){e.printStackTrace();}
            catch (ClassNotFoundException e){e.printStackTrace();}
            catch (InterruptedException e){e.printStackTrace();}
        }
    }

    private void inviaIscritti(StatoSensore stato) {
        try {
            for (Integer i : lista.keySet()) {
                if (i != stato.getId()) {  //mando la notifica ad un iscritto
                    DatagramSocket s = new DatagramSocket();
                    byte[] buf = new byte[512];
                    String msg = stato.getId()+" "+stato.getNumProg();
                    buf = msg.getBytes();
                    DatagramPacket packet = new DatagramPacket(buf, buf.length, lista.get(i), udpClient);
                    s.send(packet);
                }
            }
        } catch (IOException e){e.printStackTrace();}
    }

    class HandlerReg extends Thread{
        private Socket s;

        public HandlerReg(Socket s) {
            this.s = s;
        }

        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                InetAddress address = s.getInetAddress();
                int id = Integer.parseInt(in.readLine());
                mutexLista.acquire();
                if (!lista.containsKey(id))
                    lista.put(id,address);
                mutexLista.release();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean verifica(StatoSensore statoSensore) {
        return !(verificaUmidita(statoSensore) && verificaAria(statoSensore));
    }

    private boolean verificaUmidita(StatoSensore statoSensore) {
        double media, numero=0, somma=0;
        for (StatoSensore s : mappa.get(statoSensore.getId())){
            numero++;
            somma+=s.getSuolo();
        }
        media = somma/numero;
        double eps = 0.05*media;
        return statoSensore.getSuolo()<=media+eps && statoSensore.getSuolo()>=media-eps;
    }

    private boolean verificaAria(StatoSensore statoSensore) {
        double media, numero=0, somma=0;
        for (Map.Entry<Integer,Stack<StatoSensore>> entry : mappa.entrySet()){
            numero++;
            somma += entry.getValue().peek().getAria();
        }
        media=somma/numero;
        double eps = 0.05*media;
        return statoSensore.getAria()<=media+eps && statoSensore.getAria()>=media-eps;
    }

    public void apriRichieste(){
        try {
            ServerSocket s = new ServerSocket(statoSensPorta);
            new Accepter(s,false).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void apriRegistrazioni(){
        try {
            ServerSocket s = new ServerSocket(richiestaNotif);
            new Accepter(s,true).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
