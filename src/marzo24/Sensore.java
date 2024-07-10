package marzo24;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Sensore {
    private int id,aria,suolo;

    public Sensore(int id, int aria, int suolo) {
        this.id = id;
        this.aria = aria;
        this.suolo = suolo;
    }

    private void inviaRichiesta(){
        try {
            Socket s = new Socket(InetAddress.getLocalHost(), 3000);
            StatoSensore statoSensore = new StatoSensore(id,aria,suolo);

            ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
            out.writeObject(statoSensore);

            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            System.out.println(in.readLine());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void iscriviti(){
        try {
            Socket s = new Socket(InetAddress.getLocalHost(), 4000);
            PrintWriter out = new PrintWriter(s.getOutputStream(),true);
            out.write(""+id);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//TODO rimani in ascolto su UDP
    public StatoSensore creaStato(){
        return new StatoSensore(id,aria,suolo);
    }

    public void setAria(int aria) {
        this.aria = aria;
    }

    public void setSuolo(int suolo) {
        this.suolo = suolo;
    }
}
