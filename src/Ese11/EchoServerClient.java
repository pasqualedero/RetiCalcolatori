package Ese11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class EchoServerClient {
    public static void main(String[] args) {
        try {
            Socket s = new SocketOpener(InetAddress.getLocalHost().getHostAddress(), 8189, 10_000).open();
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            PrintWriter out = new PrintWriter(s.getOutputStream(),true);

            new MyReader(in).start();

            while(true) {
                Scanner sc = new Scanner(System.in);
                String line = sc.nextLine();
                out.println(line);
            }
        }catch (Exception e){
            System.err.println(e);
        }
    }
}

class SocketOpener extends Thread{
    private Socket s;
    private int port, timeout;
    private String host;

    public SocketOpener(String host, int port, int timeout) {
        this.port=port;
        this.host=host;
        this.timeout=timeout;
    }

    public Socket open(){
        try {
            this.start();
            this.join(timeout);
        }catch (Exception e) {
        }
        System.out.println("Connessione creata");
        return s;
    }

    @Override
    public void run(){
        try {
            s=new Socket(host,port);
        }catch (Exception e){
        }
    }
}

class MyReader extends Thread {
    BufferedReader in;

    public MyReader(BufferedReader in) {
        this.in = in;
    }

    @Override
    public void run() {
        boolean more = true;
        try {
            while (more) {
                String line = in.readLine();
                if (line == null)
                    more = false;
                else
                    System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("Error" + e);
        }
    }

}
