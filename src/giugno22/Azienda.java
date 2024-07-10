package giugno22;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Random;

public class Azienda extends Thread{
    private int iva;
    private LinkedList<Offerta> offerte;

    public Azienda(int iva, LinkedList<Offerta> offerte) {
        this.iva = iva;
        this.offerte = offerte;
    }

    @Override
    public void run() {
        try{
            Socket s = new Socket(InetAddress.getByName("job.unical.it"),3000);
            ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
            out.writeObject(offerte.get(new Random().nextInt(5)));
            out.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            int id = Integer.parseInt(in.readLine());
            in.close();

            ServerSocket serverSocket = new ServerSocket(5000);
            Socket socket = serverSocket.accept();

            BufferedReader inn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String candidatura = inn.readLine();
            inn.close(); socket.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        LinkedList<Offerta> l = new LinkedList<>();
        l.add(new Offerta("a","b","c",12));
        Azienda a = new Azienda(1,l);
        a.start();
    }
}
