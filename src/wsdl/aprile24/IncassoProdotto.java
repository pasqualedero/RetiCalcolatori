package wsdl.aprile24;

import java.io.Serializable;

public class IncassoProdotto implements Serializable {
    private String nomeVino;
    private int idAzienda, quantita;
    private double importo;

    public IncassoProdotto(String nomeVino, int idAzienda, int quantita, double importo) {
        this.nomeVino = nomeVino;
        this.idAzienda = idAzienda;
        this.quantita = quantita;
        this.importo = importo;
    }

    public String getNomeVino() {
        return nomeVino;
    }

    public int getIdAzienda() {
        return idAzienda;
    }

    public int getQuantita() {
        return quantita;
    }

    public double getImporto() {
        return importo;
    }
}
