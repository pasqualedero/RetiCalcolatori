package giugno23;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;

public class Azienda extends Thread{
    Sondaggio sondaggio;

    public Azienda(Sondaggio sondaggi) {
        this.sondaggio = sondaggi;
    }

    @Override
    public void run() {
        try{
            Socket s = new Socket(InetAddress.getLocalHost(),3000);
            ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
            out.writeObject(sondaggio);
            out.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            String res = in.readLine();
            in.close(); s.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
