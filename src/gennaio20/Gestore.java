package gennaio20;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class Gestore {
    private static final int portaRic=1111, portaMult=2222, portaOff=3333;
    private static final String indMult = "224.3.2.1";

    public void avvia(){
        new Richieste().start();
    }

    class Richieste extends Thread{
        @Override
        public void run() {
            try{
                ServerSocket s = new ServerSocket(portaRic);
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
            try{
                BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                String req = in.readLine();
                in.close();

                MulticastSocket m = new MulticastSocket();
                byte[] buf = new byte[512];
                buf = req.getBytes();
                DatagramPacket packet = new DatagramPacket(buf,buf.length, InetAddress.getByName(indMult),portaMult);
                m.send(packet);

                LinkedList<String> risultati = new LinkedList<>();
                HandlerCentro hc = new HandlerCentro(risultati);
                hc.start();
                hc.join();

                ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
                out.writeObject(risultati);
                out.flush();out.close();

                s.close();

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    class HandlerCentro extends Thread{
        private LinkedList<String> risultati;
        private Semaphore mutex = new Semaphore(1);

        public HandlerCentro(LinkedList<String> risultati) {
            this.risultati = risultati;
        }

        @Override
        public void run() {
            try{
                ServerSocket s = new ServerSocket(portaOff);
                s.setSoTimeout(60_000);

                while (true){
                    try {
                        new HandlerOfferta(risultati, mutex, s.accept()).start();
                    } catch (SocketTimeoutException so){
                        break;
                    }
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    class HandlerOfferta extends Thread{
        private LinkedList<String> list;
        private Semaphore mutex;
        private Socket s;

        public HandlerOfferta(LinkedList<String> list, Semaphore mutex, Socket s) {
            this.list = list;
            this.mutex = mutex;
            this.s=s;
        }

        @Override
        public void run() {
            try{
                BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                String offerta= in.readLine();
                in.close();

                mutex.acquire();
                list.add(offerta);
                mutex.release();

                s.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new Gestore().avvia();
    }

}
