package gareAppalto;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;

public class Richiesta implements Serializable {
    private String descr;
    private int impMax;

    public Richiesta(String descr, int impMax) {
        this.descr = descr;
        this.impMax = impMax;
    }

    public String getDescr() {
        return descr;
    }

    public int getImpMax() {
        return impMax;
    }

    @Override
    public String toString() {
        return "Richiesta{" +
                "descr='" + descr + '\'' +
                ", impMax=" + impMax +
                '}';
    }


}
