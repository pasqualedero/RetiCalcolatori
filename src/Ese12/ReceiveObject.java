package Ese12;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class ReceiveObject {
    private static LinkedList<Studente> lista = new LinkedList<>();
    public static void main(String[] args) {
        try {
            // Creo degli studenti

            for (int i = 0; i < 10; i++) {
                lista.add(new Studente("a"+i, "b"+i, i, i+2000, true));
            }
            // Apro il server
            ServerSocket serverSocket = new ServerSocket(8189);
            while (true){
               Socket incoming = serverSocket.accept();
               new HandlerRequest(incoming,lista).start();
               System.out.println("Connessione avvenuta con successo");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

class HandlerRequest extends Thread{
    private Socket socket;
    private List<Studente> lista;

    public HandlerRequest(Socket socket, List<Studente> lista){
        this.socket=socket;
        this.lista=lista;
    }

    @Override
    public void run() {
        try {
            // istanzio lettori e scrittori
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            // leggo le requests del client
            while(true){
                String matricola = in.readLine();
                Integer mat = Integer.parseInt(matricola);
                System.out.println("Tradotta! "+mat);
                if (mat==0)
                    break;
                Studente stud = cercaMatricola(mat);
                System.out.println(stud);
                if (stud!=null){
                    System.out.println("Studente "+stud+" inviato");
                    out.writeObject(stud);
                }else
                    out.writeObject("No such student");
                out.flush();
            }
            socket.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private Studente cercaMatricola(int matricola) {
        for (Studente s:lista) {
            if (s.getMat()==matricola)
                return s;
        }
        return null;
    }

}
