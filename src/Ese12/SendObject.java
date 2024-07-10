package Ese12;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class SendObject {
    public static void main(String[] args) throws UnknownHostException {
        try{
            int porta = 8189;
            String host = InetAddress.getLocalHost().getHostAddress();
            Socket s = new Handler(host,porta,4).open();
            if (s==null){
                System.err.println("Couldn't establish a connection");
                return;
            }
            boolean stop = false;

            PrintWriter out = new PrintWriter(s.getOutputStream(),true);
            ObjectInputStream in = new ObjectInputStream(s.getInputStream());

            new MyReader(in).start();
            Scanner sc = new Scanner(System.in);

            while(!stop){
                String line = sc.nextLine();
                if (line.equals("0"))
                    stop=true;
                if (!line.matches("[0-9]*"))
                    System.err.println("Not expected");
                out.println(line);
                System.out.println("inviata la matricola");
            }
            s.close();
        }catch (IOException e){
            System.err.println(e);
        }
    }
}

class Handler extends Thread{
    private String host;
    private int porta, timeout;
    private Socket s;

    public Handler(String host, int porta, int timeout) {
        this.host = host;
        this.porta = porta;
        this.timeout = timeout;
    }

    public Socket open(){
        try {
            this.start();
            this.join(timeout);
        }catch (Exception e){
            System.err.println("Timeout expired");
        }
        return s;
    }

    @Override
    public void run(){
        try {
            s=new Socket(host,porta);
        }catch (Exception e){
            System.err.println(e);
        }
    }
}

class MyReader extends Thread{
    private ObjectInputStream in;

    public MyReader(ObjectInputStream in) {
        this.in = in;
    }

    @Override
    public void run(){
        try{
            while(true){
                Studente stud = (Studente) in.readObject();
                System.out.println(stud);
            }
        }catch (Exception e){
            System.err.println(e);
        }
    }
}