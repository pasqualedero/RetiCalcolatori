package gennaio20;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.util.Random;

public class Centro extends Thread{
    private static final int portaRic=1111, portaMult=2222, portaOff=3333;
    private static final String indMult = "224.3.2.1";

    @Override
    public void run() {
        try{
            MulticastSocket m = new MulticastSocket(portaMult);
            m.joinGroup(InetAddress.getByName(indMult));
            byte[] buf = new byte[512];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            m.receive(packet);

            String req = new String(packet.getData());
            m.close();

            Socket s = new Socket(InetAddress.getByName("gestore.dimes.unical.it"),portaOff);
            String hostname = s.getLocalAddress().getHostName();
            String offerta = "";

            if (Math.random()>=0.3){
                int prezzo = new Random().nextInt(50,150);
                offerta = hostname +"," +prezzo;
                PrintWriter pw = new PrintWriter(s.getOutputStream(),true);
                pw.write(offerta); pw.close();
            }
            s.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Centro().start();
    }
}
