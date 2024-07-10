package settembre22;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.*;
import java.util.concurrent.Semaphore;

public class Server {
    private final static int portaPren=3000, portaMul=5000, portaPres=4000;
    private final static String indMul="230.0.0.1";
    private HashMap<String,Integer> posti = new HashMap<>();
    private Semaphore mPosti = new Semaphore(1), mAuth = new Semaphore(1);
    private HashMap<Integer,Long> auth = new HashMap<>();
    private static int id=0;

    public Server(){
        posti.put("fiscale",20);
        posti.put("sociale",20);
        posti.put("catasto",20);
        posti.put("scuola",20);
    }

    public void avvia(){
        new Pren().start();
        new Pres().start();
    }

    class Hand extends Thread{
        private int id;

        public Hand(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                ServerSocket serverSocket = new ServerSocket(portaPres);
                Socket s = serverSocket.accept();
                s.setSoTimeout(30_000);

                BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                String res = in.readLine();
                StringTokenizer st = new StringTokenizer(res);
                Integer idd = Integer.parseInt(st.nextToken());
                Long ps = Long.parseLong(st.nextToken());

                if (auth.get(idd)==ps){
                    sleep(new Random().nextInt(10*60*1000,30*60*1000));
                }

            } catch (SocketTimeoutException e) {
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    class Pres extends Thread{
        @Override
        public void run() {
            try {
                Calendar now = Calendar.getInstance();
                Calendar start = Calendar.getInstance();
                start.set(Calendar.HOUR_OF_DAY, 9);
                start.set(Calendar.MINUTE, 0);
                start.set(Calendar.SECOND, 0);

                sleep(start.getTimeInMillis() - now.getTimeInMillis());

                for (Integer id : auth.keySet()) {
                    MulticastSocket m = new MulticastSocket();
                    byte[] buf = new byte[512];
                    String idd = ""+id;
                    buf = idd.getBytes();
                    DatagramPacket packet = new DatagramPacket(buf, buf.length, InetAddress.getByName(indMul),portaMul);
                    m.send(packet); m.close();

                    Hand s = new Hand(id);
                    s.start();
                    s.join();
                }
            } catch (IOException e){
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    class Pren extends Thread{
        @Override
        public void run() {
            try {
                ServerSocket s = new ServerSocket(portaPren);
                while (true){
                    new Handler(s.accept()).start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class Handler extends Thread{
        Socket s;

        public Handler(Socket s) {
            this.s = s;
        }

        @Override
        public void run() {
            try {
                Calendar now = Calendar.getInstance();
                Calendar begin = Calendar.getInstance();
                begin.set(Calendar.HOUR_OF_DAY, 8);
                begin.set(Calendar.MINUTE, 0);
                begin.set(Calendar.SECOND, 0);

                Calendar end = (Calendar) begin.clone();
                end.add(Calendar.HOUR_OF_DAY, 1);
                PrintWriter out = new PrintWriter(s.getOutputStream());


                if (now.before(end) && now.after(begin)) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    String categoria = in.readLine();

                    mPosti.acquire();
                    if (posti.get(categoria)==0) {
                        out.write("posti finiti");
                        return;
                    }
                    else {
                        int p = posti.get(categoria);
                        p--;
                        posti.put(categoria,p);
                    }
                    id++;
                    int mioId = id;
                    mPosti.release();

                    Long psw = new Random().nextLong(1000000000000000L,10000000000000000L);
                    out.write(mioId+" "+psw);

                    mAuth.acquire();
                    auth.put(mioId,psw);
                    mAuth.release();

                    in.close();

                }else
                    out.write("Fuori tempo");
                out.close(); s.close();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new Server().avvia();
    }

}
