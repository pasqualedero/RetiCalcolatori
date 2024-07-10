package Ese11;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class EchoServer {
    public static void main(String[] args) {
        try {
            ServerSocket s = new ServerSocket(8189);
            while (true){
                Socket incoming = s.accept();
                new Handler(incoming, s).start();
            }
        }catch (Exception e){
            System.err.println(e);
        }
    }
}

class Handler extends Thread{
    private Socket socket;
    private ServerSocket s;

    public Handler(Socket socket, ServerSocket s) {
        this.socket = socket;
        this.s=s;
    }

    @Override
    public void run(){
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
            System.out.println("Benvenuto! Scrivi BYE per uscire ed EXIT per chiudere il programma");
            while(true){
                String line = in.readLine();
                if (line.equals("BYE") || line.equals("") || line.equals("EXIT")){
                    break;
                }
                else {
                    out.println("Echo: "+line.toUpperCase());
                    System.out.println("Messaggio Inviato");
                }
            }
            socket.close();
        } catch (Exception e) {
            if (e.getClass()== SocketException.class)
                System.err.println("Connessione persa o annullata");
        }

    }
}