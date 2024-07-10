package gennaio23;

import java.io.*;
import java.net.*;
import java.nio.channels.MulticastChannel;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private final static int portaClient=3000, portaSensore=4000, portaMulti=5000;
    private final static String indMulti = "230.0.0.1";
    private HashMap<Integer, Stack<Misura>> misure = new HashMap<>();
    private HashMap<Integer,Thread> scadenze = new HashMap<>();

    public void avvia(){
        new AscoltoClient().start();
        new AscoltoSensore().start();
    }

    class AscoltoClient extends Thread{
        @Override
        public void run() {
            try{
                ServerSocket serverSocket = new ServerSocket(portaClient);
                while (true){
                    new Gestisci(serverSocket.accept()).start();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    class Gestisci extends Thread{
        private Socket s;

        public Gestisci(Socket s) {
            this.s = s;
        }

        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                Integer idSensore = Integer.parseInt(in.readLine());

                Misura m = null;
                if (misure.get(idSensore)!=null)
                    m = misure.get(idSensore).peek();

                ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
                out.writeObject(m);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class AscoltoSensore extends Thread{
        @Override
        public void run() {
            try{
                while (true) {
                    DatagramSocket s = new DatagramSocket(portaSensore);
                    byte[] buf = new byte[512];
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    s.receive(packet);

                    ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(packet.getData()));
                    Misura m = (Misura) in.readObject();

                    if (!misure.containsKey(m.idSensore()))
                        misure.put(m.idSensore(),new Stack<Misura>());
                    misure.get(m.idSensore()).push(m);

                    if (scadenze.containsKey(m.idSensore())) {
                        scadenze.get(m.idSensore()).interrupt();
                        scadenze.remove(m.idSensore());
                    }

                    scadenze.put(m.idSensore(),new Timer(m.idSensore()));
                    scadenze.get(m.idSensore()).start();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class Timer extends Thread{
        private int idSensore;

        public Timer(int idSensore) {
            this.idSensore = idSensore;
        }

        @Override
        public void run() {
            try {
                sleep(600_000);
                MulticastSocket multicastSocket = new MulticastSocket();
                String msg = idSensore +" non funzionante";
                byte[] buf = new byte[512];
                DatagramPacket packet = new DatagramPacket(buf, buf.length,InetAddress.getByName(indMulti),portaMulti);
                buf=msg.getBytes();
                multicastSocket.send(packet);
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
