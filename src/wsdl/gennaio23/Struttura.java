package wsdl.gennaio23;

import java.io.Serializable;

public class Struttura implements Serializable {
    private String nome, citta;
    private int stelle, posti;

    public Struttura(String nome, String citta, int stelle, int posti) {
        this.nome = nome;
        this.citta = citta;
        this.stelle = stelle;
        this.posti = posti;
    }

    public String getNome() {
        return nome;
    }

    public String getCitta() {
        return citta;
    }

    public int getStelle() {
        return stelle;
    }

    public int getPosti() {
        return posti;
    }
}
