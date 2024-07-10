package febbraio23;

import java.io.Serializable;

public class Partecipazione implements Serializable {
    private int idConcorso;
    private String nome, cognome, cf, cv;

    public Partecipazione(int idConcorso, String nome, String cognome, String cf, String cv) {
        this.idConcorso = idConcorso;
        this.nome = nome;
        this.cognome = cognome;
        this.cf = cf;
        this.cv = cv;
    }

    public int getIdConcorso() {
        return idConcorso;
    }

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public String getCf() {
        return cf;
    }

    public String getCv() {
        return cv;
    }
}
