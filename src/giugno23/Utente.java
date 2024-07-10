package giugno23;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;

public class Utente extends Thread{
    private int id;
    private String nome;
    private RisposteSondaggio risposteSondaggio;

    public Utente(int id, String nome, RisposteSondaggio risposteSondaggio) {
        this.id = id;
        this.nome = nome;
        this.risposteSondaggio = risposteSondaggio;
    }

    @Override
    public void run() {
        try{
            MulticastSocket m = new MulticastSocket(5000);
            m.joinGroup(InetAddress.getByName("230.0.0.1"));

            byte[] buf = new byte[512];

            DatagramPacket packet = new DatagramPacket(buf,buf.length);
            m.receive(packet);
            m.close();

            String res = new String(packet.getData());

            Socket s = new Socket(InetAddress.getLocalHost(),4000);
            PrintWriter out = new PrintWriter(s.getOutputStream(),true);
            out.write(id);
            out.close();

            ObjectInputStream in = new ObjectInputStream(s.getInputStream());
            Sondaggio sondaggio = (Sondaggio) in.readObject();
            in.close();

            ObjectOutputStream outObj = new ObjectOutputStream(s.getOutputStream());
            outObj.writeObject(risposteSondaggio);
            outObj.close(); s.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Utente u = new Utente(1,"p", new RisposteSondaggio());
        u.start();
    }
}
