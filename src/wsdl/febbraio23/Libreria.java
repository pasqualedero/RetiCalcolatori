package wsdl.febbraio23;

import java.io.Serializable;

public class Libreria implements Serializable {
    private String partivaIva,nome,citta;

    public Libreria(String partivaIva, String nome, String citta) {
        this.partivaIva = partivaIva;
        this.nome = nome;
        this.citta = citta;
    }
}
