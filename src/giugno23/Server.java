package giugno23;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class Server {
    private final static int portaAziende=3000, portaMulti=5000, portaUtenti=4000;
    private final static String indirizzoMulti="230.0.0.1";
    private Semaphore mutexS=new Semaphore(1),mutexR = new Semaphore(1);
    private HashMap<Integer,Record> sondaggi = new HashMap<>();
    private HashMap<Integer,LinkedList<RisposteSondaggio>> mappaRisposte = new HashMap<>();

    public void avvia(){
        new AscoltoAziende().start();
        new AscoltoUtenti().start();
    }

    class AscoltoUtenti extends Thread{
        @Override
        public void run() {
            try {
                ServerSocket s = new ServerSocket(portaUtenti);
                while (true){
                    new HandlerUtenti(s.accept()).start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class HandlerUtenti extends Thread{
        private Socket s;

        public HandlerUtenti(Socket s) {
            this.s = s;
        }

        @Override
        public void run() {
            try{
                BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                int id = Integer.parseInt(in.readLine());
                in.close();

                mutexS.acquire();
                boolean aperto = sondaggi.get(id).isAperto();
                mutexS.release();

                ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
                if (aperto){
                    out.writeObject(sondaggi.get(id).getSondaggio());
                }else{
                    out.writeChars("");
                }
                out.close();

                ObjectInputStream inn = new ObjectInputStream(s.getInputStream());
                RisposteSondaggio res = (RisposteSondaggio) inn.readObject();
                inn.close();

                mutexR.acquire();
                if (!mappaRisposte.containsKey(id))
                    mappaRisposte.put(id, new LinkedList<>());
                mappaRisposte.get(id).add(res);
                mutexR.release();

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    class AscoltoAziende extends Thread{
        @Override
        public void run() {
            try {
                ServerSocket s = new ServerSocket(portaAziende);
                while (true){
                    new HandlerAzienda(s.accept()).start();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    class HandlerAzienda extends Thread{
        private Socket s;

        public HandlerAzienda(Socket s) {
            this.s = s;
        }

        @Override
        public void run() {
            try {
                ObjectInputStream in = new ObjectInputStream(s.getInputStream());
                Sondaggio sondaggio = (Sondaggio) in.readObject();

                mutexS.acquire();
                sondaggi.put( sondaggio.getId(),new Record(sondaggio));
                mutexS.release();

                MulticastSocket multicastSocket = new MulticastSocket();
                byte[] buf = new byte[512];
                String res = sondaggio.getId()+" "+sondaggio.getNome();
                buf = res.getBytes();

                DatagramPacket packet= new DatagramPacket(buf,buf.length,InetAddress.getByName(indirizzoMulti),portaMulti);
                multicastSocket.send(packet);

                in.close();
                new Timeout(s,sondaggio).start();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class Timeout extends Thread{
        private Socket s;
        private Sondaggio sondaggio;

        public Timeout(Socket s, Sondaggio sondaggio) {
            this.s = s;
            this.sondaggio = sondaggio;
        }

        @Override
        public void run() {
            try {
                sleep(3600*1000);
                mutexS.acquire();
                sondaggi.get(sondaggio.getId()).setAperto(false);
                mutexS.release();

                String res="";
                LinkedList<RisposteSondaggio> risposte = mappaRisposte.get(sondaggio.getId());
                for (int i=0; i<sondaggio.getListaDomande().size();i++){
                    int si=0,no=0;
                    for (int j=0; j<risposte.size();j++){
                        if (risposte.get(j).getRisposte().get(i)=="si")
                            si++;
                        else no++;
                    }
                    res = "Domanda "+i+" si:"+si+" no:"+no;
                }

                PrintWriter out = new PrintWriter(s.getOutputStream(),true);
                out.write(res);

                s.close(); out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Server s = new Server();
        s.avvia();
    }

}
