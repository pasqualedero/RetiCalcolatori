package febbraio24;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.concurrent.Semaphore;

public class Server {
    private final static int portaAscolto=3000, portaNotifica=4000, portaUDP=4000;
    private static int id=0;
    private Semaphore mutex = new Semaphore(1);
    private LinkedList<Canzone> canzoni = new LinkedList<>();




    //costruttore di default

    public void avvia(){
        new Ascolto().start();
        new Iscrizioni().start();

    }

    class Iscrizioni extends Thread{
        @Override
        public void run() {
            try {
                ServerSocket s = new ServerSocket(portaNotifica);
                while (true){
                    new HandlerNotifica(s.accept()).start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class HandlerNotifica extends Thread{
        Socket s;

        public HandlerNotifica(Socket s) {
            this.s = s;
        }

        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                String input = in.readLine();

                LinkedList<String> parole = new LinkedList<>();
                StringTokenizer st = new StringTokenizer(input);
                while (st.hasMoreTokens()){
                    parole.add(st.nextToken());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class Ascolto extends Thread{
        @Override
        public void run() {
            try {
                ServerSocket serverSocket = new ServerSocket(portaAscolto);
                while (true) {
                    new Handler(serverSocket.accept()).start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class Handler extends Thread{
        private Socket s;

        public Handler(Socket s) {
            this.s = s;
        }

        @Override
        public void run() {
            try{
                PrintWriter out = new PrintWriter(s.getOutputStream(),true);
                int ora = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

                if (ora <= 13 && ora >= 8) {
                    ObjectInputStream in = new ObjectInputStream(s.getInputStream());
                    Canzone canzone = (Canzone) in.readObject();

                    if (!rifiutata(canzone)){
                        mutex.acquire();
                        canzoni.add(canzone);
                        mutex.release();
                        out.write(++id);
                    } else out.write("Rifiutata");


                }else{
                    out.write("Rifiutata");
                }
                out.close(); s.close();

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private boolean rifiutata(Canzone canzone) {
        return verifica(canzone) && verifica2(canzone);
    }

    private boolean verifica2(Canzone canzone) {
        String contenuto = canzone.getTesto();
        for (Canzone c : canzoni){
            String c1 = c.getTesto();
            if (verifica3(c1,contenuto))
                return true;
        }
        return false;
    }

    private boolean verifica3(String c1, String c2) {
        for (int i = 0; i < c1.length()-50 ; i++) {
            String s1 = c1.substring(0+i,50+i);
            for (int j=0; j < c2.length()-50; j++){
                String s2 = c2.substring(0+j,50+j);
                if (s1.equals(s2))
                    return true;
            }
        }
        return false;
    }

    private boolean verifica(Canzone canzone) {
        for (Canzone c : canzoni){
            if (c.getTitolo()== canzone.getTitolo()){
                for (String autore : c.getAutori()){
                    if (canzone.getAutori().contains(autore))
                        return true;
                }
            }
        }
        return false;
    }

}
