package gennaio23;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Date;

public class Sensore extends Thread{
    private int idSensore;

    public Sensore(int idSensore) {
        this.idSensore = idSensore;
    }

    @Override
    public void run() {
        try{
            while (true) {
                DatagramSocket s = new DatagramSocket();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(baos);
                out.writeObject(new Misura(1, 90, new Date()));
                byte[] buf = baos.toByteArray();

                DatagramPacket packet = new DatagramPacket(buf, buf.length, InetAddress.getLocalHost(), 4000);
                s.send(packet);

                sleep(300_000);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
