package gennaio20;

import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;

public class Cliente extends Thread{
    private String data;
    private int n;

    public Cliente(String data, int n) {
        this.data = data;
        this.n = n;
    }

    private static final int portaRic=1111, portaMult=2222, portaOff=3333;
    private static final String indMult = "224.3.2.1";

    @Override
    public void run() {
        try{
            Socket s = new Socket(InetAddress.getByName("gestore.dimes.unical.it"),portaRic);

            PrintWriter out = new PrintWriter(s.getOutputStream(),true);
            out.write(data+","+n);
            out.close();

            ObjectInputStream in = new ObjectInputStream(s.getInputStream());
            LinkedList<String> list = (LinkedList<String>) in.readObject();
            in.close();
            s.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Cliente("3feb",10).start();
    }
}
