package aprile23;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Cliente extends Thread{
    private String cf;
    private int porta;
    private final static int portaMulti=5000, miaUDP=4000;
    private final static String indirizzoMulti = "230.0.0.1";
    private ArrayList<Integer> aste = new ArrayList<>();

    public Cliente(String cf) {
        this.cf = cf;
    }

    @Override
    public void run() {
        try {
            MulticastSocket ms = new MulticastSocket(portaMulti);
            ms.joinGroup(InetAddress.getByName(indirizzoMulti));


                byte[] buf = new byte[512];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                ms.receive(packet);

                String msg = new String(packet.getData());
                System.out.println(msg);
                StringTokenizer st = new StringTokenizer(msg);

                aste.add(Integer.parseInt(st.nextToken()));
                this.porta=Integer.parseInt(st.nextToken());


                Socket socket = new Socket(InetAddress.getLocalHost(), porta);
                System.out.println("fatto2");
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                out.writeObject(new Offerta(cf,1,1000));

                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                //String esito = in.readLine();

                DatagramSocket ds = new DatagramSocket(miaUDP);
                byte[] buff = new byte[512];
                DatagramPacket packett = new DatagramPacket(buf, buf.length);
                ds.receive(packett);
                System.out.println("fine "+new String(packett.getData()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Cliente c = new Cliente("paco");
        c.start();
    }
}
