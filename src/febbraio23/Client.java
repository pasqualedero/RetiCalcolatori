package febbraio23;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.Calendar;
import java.util.Date;

public class Client extends Thread{
    private String nome,cognome,cv,cf;
    int[] concorsi;

    public Client(String nome, String cognome, String cv, String cf, int[] concorsi) {
        this.nome = nome;
        this.cognome = cognome;
        this.cv = cv;
        this.cf = cf;
        this.concorsi = concorsi;
    }

    @Override
    public void run() {
        try{
            Socket s = new Socket(InetAddress.getLocalHost(), 3000);

            ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
            out.writeObject(new Partecipazione(1,"a","b","c","d"));

            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            int id = Integer.parseInt(in.readLine());

            MulticastSocket m = new MulticastSocket(5000);
            m.joinGroup(InetAddress.getByName("230.0.0.1"));
            byte[] buf = new byte[512];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            m.receive(packet);

            String res = new String(packet.getData());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Calendar c = Calendar.getInstance();
        Date d = new Date();
        System.out.println(d.toString());
    }
}
