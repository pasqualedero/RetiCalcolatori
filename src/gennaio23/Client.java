package gennaio23;

import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;

public class Client extends Thread{
    private int idSensore;

    public Client(int idSensore) {
        this.idSensore = idSensore;
    }

    @Override
    public void run() {
        new Invia().start();
        new Ascolta().start();
    }

    class Invia extends Thread{
        @Override
        public void run() {
            try {
                Socket s = new Socket(InetAddress.getLocalHost(), 3000);
                PrintWriter out = new PrintWriter(s.getOutputStream(),true);
                out.write(idSensore);

                ObjectInputStream in = new ObjectInputStream(s.getInputStream());
                Misura misura = (Misura) in.readObject();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    class Ascolta extends Thread{
        @Override
        public void run() {
            try {
                MulticastSocket multicastSocket = new MulticastSocket(5000);
                multicastSocket.joinGroup(InetAddress.getByName("230.0.0.1"));

                byte[] buf = new byte[512];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                multicastSocket.receive(packet);

                String res = new String(packet.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Client c = new Client(1);
        c.start();
    }
}
