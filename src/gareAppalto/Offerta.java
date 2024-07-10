package gareAppalto;

import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;

public class Offerta implements Serializable {
    private String id;
    private int importo;

    public Offerta(int importo, String id) {
        this.importo = importo;
        this.id=id;
    }

    public String getId() {
        return id;
    }

    public int getImporto() {
        return importo;
    }

    @Override
    public String toString() {
        return "Offerta{" +
                "id='" + id + '\'' +
                ", importo=" + importo +
                '}';
    }


}
