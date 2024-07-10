package settembre22;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.util.StringTokenizer;

public class Client extends Thread{
    @Override
    public void run() {
        try {
            Socket s = new Socket(InetAddress.getByName("servizi.unical.it"),3000);
            PrintWriter out = new PrintWriter(s.getOutputStream(),true);
            out.write("scuola");
            out.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            String res = in.readLine();
            StringTokenizer st = new StringTokenizer(res);
            int id = Integer.parseInt(st.nextToken());
            long psw = Long.parseLong(st.nextToken());
            in.close();s.close();

            MulticastSocket m = new MulticastSocket(5000);
            m.joinGroup(InetAddress.getByName("230.0.0.1"));
            byte[] buf = new byte[512];
            DatagramPacket packet = new DatagramPacket(buf,buf.length);
            m.receive(packet);
            m.close();

            Socket ss = new Socket(InetAddress.getByName("servizi.unical.it"),4000);
            PrintWriter outt = new PrintWriter(ss.getOutputStream());
            outt.write(id+" "+psw);
            outt.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        new Client().start();
    }
}
