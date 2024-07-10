package wsdl.luglio23;

import java.io.Serializable;

public class PrezzoProdotto implements Serializable {
    private String partitaIva, nomeOrtaggio;
    private double prezzo;

    public PrezzoProdotto(String partitaIva, String nomeOrtaggio, double prezzo) {
        this.partitaIva = partitaIva;
        this.nomeOrtaggio = nomeOrtaggio;
        this.prezzo = prezzo;
    }

    public String getPartitaIva() {
        return partitaIva;
    }

    public String getNomeOrtaggio() {
        return nomeOrtaggio;
    }

    public void setPrezzo(double prezzo) {
        this.prezzo = prezzo;
    }

    public double getPrezzo() {
        return prezzo;
    }
}
