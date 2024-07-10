package giugno22;

import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;

public class Cliente extends Thread{
    private  int id;
    private String offerta;

    public Cliente(int id, String offerta) {
        this.id = id;
        this.offerta = offerta;
    }

    @Override
    public void run() {
        try{
            MulticastSocket m = new MulticastSocket(6000);
            m.joinGroup(InetAddress.getByName("230.0.0.1"));

            byte[] buf = new byte[512];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            m.receive(packet);
            m.close();

            String res = new String(packet.getData());

            Socket s = new Socket(InetAddress.getByName("job.unical.it"),4000);
            PrintWriter out = new PrintWriter(s.getOutputStream());
            out.write(id+" "+offerta);
            out.close(); s.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Cliente c = new Cliente(3,"a");
        c.start();
    }
}
