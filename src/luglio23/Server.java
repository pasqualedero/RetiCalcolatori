package luglio23;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.concurrent.Semaphore;

public class Server {
    private final static String gAddress="230.0.0.1";
    private final static int gPort=5000, cPort=4000, nPort = 3000, invioC=6000;
    private HashMap<String,HashMap<Integer,TreeSet<Offerta>>> dati = new HashMap<>();
    private HashMap<String,HashMap<Integer,TreeSet<InetAddress>>> clienti = new HashMap<>();
    private Semaphore mutexDati = new Semaphore(1);
    private Semaphore mutexClienti = new Semaphore(1);

    class GestioneOfferte extends Thread{ // un thread dedicato per ogni negozio
        Socket connessione;
        Offerta offerta;

        public GestioneOfferte(Socket connessione){
            this.connessione=connessione;
        }

        @Override
        public void run() {
            try {
                ObjectInputStream in = new ObjectInputStream(connessione.getInputStream());
                offerta = (Offerta) in.readObject();
                in.close();
                if (aggiungi(offerta)){
                    inviaMulticast(offerta);
                    notificaClienti(offerta);
                }
            }catch (IOException e){e.printStackTrace();}
            catch (ClassNotFoundException e){System.out.println("casting sbagliato");}
            catch (InterruptedException e) {throw new RuntimeException(e);}
        }
    }

    private void notificaClienti(Offerta offerta) {
        TreeSet<InetAddress> list = clienti.get(offerta.getNegozio().getNazione()).get(offerta.getProdotto());
        for (InetAddress cliente : list){
            try {
                DatagramSocket connessione = new DatagramSocket();
                byte[] buf = new byte[256];
                String m = offerta.toString();
                buf = m.getBytes();
                DatagramPacket packet = new DatagramPacket(buf,buf.length,cliente,invioC);
                connessione.send(packet);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void inviaMulticast(Offerta offerta) {
        try{
            MulticastSocket multicastSocket = new MulticastSocket(gPort);
            byte[] buf = new byte[512];
            String msg = "prodotto:"+offerta.getProdotto()+" prezzo:"+offerta.getPrezzo()+" iva:"+offerta.getNegozio().getIva()+" nazione:"+offerta.getNegozio().getNazione();
            buf = msg.getBytes();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, InetAddress.getByName(gAddress),gPort);
            multicastSocket.send(packet);
            multicastSocket.close();
        }catch (IOException e){e.printStackTrace();}
    }

    private boolean aggiungi(Offerta offerta) throws InterruptedException { //true se Negozi replicano con nuova offerta
        String nazione = offerta.getNegozio().getNazione();
        int prodotto = offerta.getProdotto();
        mutexDati.acquire();
        if (!dati.containsKey(nazione))
            dati.put(nazione,new HashMap<Integer,TreeSet<Offerta>>());
        if (!dati.get(nazione).containsKey(prodotto))
            dati.get(nazione).put(prodotto,new TreeSet<Offerta>((o1,o2)->Integer.compare(o1.getPrezzo(),o2.getPrezzo())));
        if (offerta.getQta()==0){
            for (Offerta o :  dati.get(nazione).get(prodotto)){
                if (o.getNegozio().getIva()==offerta.getNegozio().getIva())
                    dati.get(nazione).get(prodotto).remove(o);
            }
        } else
            dati.get(nazione).get(prodotto).add(offerta);
        mutexDati.release();
        return  dati.get(nazione).get(prodotto).pollFirst().equals(offerta);
    }

    public void riceviOfferta(){
        try{
            ServerSocket negoziSocket = new ServerSocket(nPort);
            System.out.println("Pronto a ricevere negozi");
            new AvviaServer(negoziSocket).start();
        }catch (IOException e){e.printStackTrace();}
    }

    class AvviaServer extends Thread{ // mi fa stare sempre in ascolto lato negozi
        private ServerSocket s;

        public AvviaServer(ServerSocket s) {
            this.s = s;
        }

        @Override
        public void run() {
           while (true){
               try {
                   Socket connessione = s.accept();
                   new GestioneOfferte(connessione).start();
               } catch (IOException e) {
                   throw new RuntimeException(e);
               }
           }
        }
    }

    public void inviaInfo(){
        try {
            ServerSocket clientSocket = new ServerSocket(cPort);
            System.out.println("Pronto a ricevere clienti");
            new AvviaRisposte(clientSocket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class AvviaRisposte extends Thread{ // mi fa stare in ascolto lato clienti
        private ServerSocket s;

        public AvviaRisposte(ServerSocket s) {
            this.s = s;
        }

        @Override
        public void run() {
           while (true){
               try {
                   new ServizioUtente(s.accept()).start();
               } catch (IOException e) {
                   throw new RuntimeException(e);
               }
           }
        }
    }

    class ServizioUtente extends Thread{ // nuovo thread per ogni cliente
        private Socket s;

        public ServizioUtente(Socket s) {
            this.s = s;
        }

        @Override
        public void run() {
            accogliRichiesta(s);
        }
    }

    private void accogliRichiesta(Socket s) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            PrintWriter out = new PrintWriter(s.getOutputStream(), true);

            StringTokenizer st = new StringTokenizer(in.readLine());
            Integer prodotto = Integer.parseInt(st.nextToken());
            String nazione = st.nextToken();
            Boolean auto = Boolean.parseBoolean(st.nextToken());

            Offerta res = dati.get(nazione).get(prodotto).pollFirst();
            String toSend = "";
            if (res != null)
                toSend = res.toString();
            out.write(toSend);
            out.close();
            in.close();
            if (auto){
                mutexClienti.acquire();
                if (!clienti.containsKey(nazione))
                    clienti.put(nazione,new HashMap<Integer,TreeSet<InetAddress>>());
                if (!clienti.get(nazione).containsKey(prodotto))
                    clienti.get(nazione).put(prodotto,new TreeSet<InetAddress>());
                clienti.get(nazione).get(prodotto).add(s.getInetAddress());
                mutexDati.release();
            }
        } catch (IOException e){e.printStackTrace();}
        catch (InterruptedException e){e.printStackTrace();}
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.riceviOfferta();
        server.inviaInfo();

    }


}
