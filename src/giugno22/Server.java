package giugno22;

import java.io.*;
import java.net.*;
import java.time.Duration;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.concurrent.Semaphore;

public class Server {
    private final static int portaAzienda=3000, portaCLiente=4000, portaBroad=6000, portaInvio=5000;
    private static final String indirizzoMult="230.0.0.1";
    private HashMap<Integer,Record> mappa = new HashMap<>();
    private Semaphore mutex = new Semaphore(1);
    private static int id=0;

    public void avvia(){
        new AscoltaAzienda().start();
        new AscoltaReq().start();
    }

    class AscoltaAzienda extends Thread{
        @Override
        public void run() {
            try {
                ServerSocket serverSocket = new ServerSocket(portaAzienda);
                while (true){
                    new Handler(serverSocket.accept()).start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class Handler extends Thread{
        private Socket s;

        public Handler(Socket s) {
            this.s = s;
        }

        @Override
        public void run() {
            try{
                ObjectInputStream in = new ObjectInputStream(s.getInputStream());
                Offerta offerta = (Offerta) in.readObject();
                in.close();

                PrintWriter out = new PrintWriter(s.getOutputStream(),true);

                mutex.acquire();
                ++id;
                int mioId = id;
                mutex.release();

                out.write(mioId);
                out.close();

                mutex.acquire();
                mappa.put(mioId,new Record(offerta,true,s.getInetAddress()));
                mutex.release();

                MulticastSocket multicastSocket = new MulticastSocket();
                byte[] buf = new byte[512];
                String res = mioId+" "+offerta.getSettore()+" "+offerta.getTipo()+" "+offerta.getRal();
                buf = res.getBytes();
                DatagramPacket packet = new DatagramPacket(buf, buf.length, InetAddress.getByName(indirizzoMult),portaBroad);
                multicastSocket.send(packet);

                new Timeout(mioId).start();

                multicastSocket.close();s.close();

            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class Timeout extends Thread{
        private int id;

        public Timeout(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                sleep(Duration.ofDays(30));
                mutex.acquire();
                mappa.get(id).setAttiva(false);
                mutex.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class AscoltaReq extends Thread{
        @Override
        public void run() {
            try {
                ServerSocket serverSocket = new ServerSocket(portaCLiente);
                while (true){
                    new ReqHandler(serverSocket.accept()).start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class ReqHandler extends Thread{
        private Socket s;

        public ReqHandler(Socket s) {
            this.s = s;
        }

        @Override
        public void run() {
            try{
                BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                String candidatura = in.readLine();
                in.close();

                StringTokenizer st = new StringTokenizer(candidatura);
                int id = Integer.parseInt(st.nextToken());
                String cv = st.nextToken();

                if (mappa.get(id).isAttiva()){
                    Socket s = new Socket(mappa.get(id).getIndirizzo(),portaInvio);
                    PrintWriter out = new PrintWriter(s.getOutputStream(),true);
                    out.write(candidatura);
                    out.close(); s.close();
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Server s = new Server();
        s.avvia();
    }

}
