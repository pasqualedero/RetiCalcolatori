package febbraio23;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.*;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
    private final static int portaAccetta=3000, portaCancella=4000, portaMulticast=5000;
    private final static String indirizzoMult="230.0.0.1";
    private HashMap<Concorso, LinkedList<Partecipazione>> mappa;
    private HashMap<Integer,Partecipazione> partecipazioneHashMap = new HashMap<>();
    private static int id=0;

    public Server(HashMap<Concorso, LinkedList<Partecipazione>> mappa) {
        this.mappa = mappa;
    }

    public void avvia(){
        try {
            ServerSocket s = new ServerSocket(portaAccetta);
            new Ricevi(s).start();
            new RiceviCancellazioni().start();
            for (Concorso c : mappa.keySet()){
                new DaiEsito(c).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class DaiEsito extends Thread{
        private Concorso concorso;

        public DaiEsito(Concorso concorso) {
            this.concorso = concorso;
        }

        @Override
        public void run() {
            try {
                Calendar now = Calendar.getInstance();
                sleep(concorso.getDataScadenza().getTimeInMillis() - now.getTimeInMillis());

                MulticastSocket s = new MulticastSocket(portaMulticast);
                s.joinGroup(InetAddress.getByName(indirizzoMult));
                byte[] buf = new byte[512];
                String res = concorso.getId()+" "+mappa.get(concorso.getId()).get(0);
                buf = res.getBytes();
                DatagramPacket packet = new DatagramPacket(buf, buf.length,InetAddress.getByName(indirizzoMult),portaMulticast);
                s.send(packet);

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    class RiceviCancellazioni extends Thread{
        @Override
        public void run() {
            try {
                while (true) {
                    DatagramSocket socket = new DatagramSocket(portaCancella);
                    byte[] buf = new byte[512];
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);

                    String res = new String(packet.getData());
                    int idCliente = Integer.parseInt(res);
                    Partecipazione p = partecipazioneHashMap.get(idCliente);
                    int idConcorso = p.getIdConcorso();

                    Concorso c = null;
                    for (Concorso concorso: mappa.keySet()){
                        if (concorso.getId()==idConcorso){
                            c=concorso;
                        }
                    }

                    if (Calendar.getInstance().before(c.getDataScadenza())){
                        mappa.get(c).remove(p);
                    }

                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    class Ricevi extends Thread{
        private ServerSocket s;

        public Ricevi(ServerSocket s) {
            this.s = s;
        }

        @Override
        public void run() {
            try{
                while (true){
                    Socket socket =s.accept();

                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                    Partecipazione partecipazione = (Partecipazione) in.readObject();
                    int idConcorso = partecipazione.getIdConcorso();
                    Concorso concorso = null;
                    for (Concorso c : mappa.keySet()){
                        if (c.getId()==idConcorso)
                            concorso=c;
                    }

                    PrintWriter out = new PrintWriter(socket.getOutputStream(),true);

                    if (!Calendar.getInstance().before(concorso.getDataScadenza())){
                        out.write("NOT_ACCEPTED");
                    }

                    out.write(++id+" "+Calendar.getInstance().toString());
                    partecipazioneHashMap.put(id,partecipazione);
                    mappa.get(concorso).add(partecipazione);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
