package luglio23;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;

public class Cliente extends Thread{
    private int id;
    private static final int portaServer=4000, portaMia=6000;
    private static InetAddress serverAddress;

    public Cliente(int id, InetAddress serverAddress) {
        this.id = id;
        this.serverAddress = serverAddress;
    }

    public void inviaInfo(int prod, String nazione, boolean iscrizione){
        try {
            Socket connessione = new Socket(serverAddress, portaServer);
            PrintWriter out = new PrintWriter(connessione.getOutputStream(),true);
            String res = prod+" "+nazione+" "+iscrizione;
            out.write(res);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() { // in ascolto di notifiche
        try {
            DatagramSocket socket = new DatagramSocket(portaMia);
            byte[] buf = new byte[512];
            while (true) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                String res = new String(packet.getData());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

    }
}
