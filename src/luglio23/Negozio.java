package luglio23;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

public class Negozio extends Thread{
    private int iva;
    private String nazione;
    private int[] prodotti;
    private static final int portaServer=3000;
    private static InetAddress address;
    private final static String gAddress="230.0.0.1";
    private final static int gPort=5000;

    public Negozio(int iva, String nazione, int[] prodotti, InetAddress addres) {
        this.iva = iva;
        this.nazione = nazione;
        this.prodotti = prodotti;
        this.address = addres;
    }

    public int getIva() {
        return iva;
    }

    public String getNazione() {
        return nazione;
    }

    public int[] getProdotti() {
        return prodotti;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Negozio negozio = (Negozio) o;
        return iva == negozio.iva && Objects.equals(nazione, negozio.nazione) && Arrays.equals(prodotti, negozio.prodotti);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(iva, nazione);
        result = 31 * result + Arrays.hashCode(prodotti);
        return result;
    }

    private void mandaRichiesta(Offerta offerta){
        try {
            Socket s = new Socket(address,portaServer);
            ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
            out.writeObject(offerta); out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(){ // rimane in ascolto sul gruppo
        try{
            MulticastSocket s = new MulticastSocket(gPort);
            InetAddress group = InetAddress.getByName(gAddress);
            s.joinGroup(group);

            while (true){
                byte[] buf = new byte[512];
                DatagramPacket packet = new DatagramPacket(buf,buf.length);
                s.receive(packet);
                System.out.println("Ricevuta");
                if (new Random().nextInt(10+1)+1 <= 5)
                    mandaRichiesta(new Offerta());
            }

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            int[] prodotti = {11, 22, 33};
            Negozio n = new Negozio(1234, "ITA", prodotti, InetAddress.getLocalHost());
            n.mandaRichiesta(new Offerta());
        } catch (IOException e){e.printStackTrace();}
    }
}
