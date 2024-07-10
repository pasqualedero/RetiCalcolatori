package Ese12;

import java.io.Serializable;

public class Studente implements Serializable {
    private String nome, cognome;
    private int mat, annoCorso;
    private boolean inCorso;

    public Studente(String nome, String cognome, int mat, int annoCorso, boolean inCorso) {
        this.nome = nome;
        this.cognome = cognome;
        this.mat = mat;
        this.annoCorso = annoCorso;
        this.inCorso = inCorso;
    }

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public int getMat() {
        return mat;
    }

    public int getAnnoCorso() {
        return annoCorso;
    }

    public boolean isInCorso() {
        return inCorso;
    }

    @Override
    public String toString() {
        return "Studente{" +
                "nome='" + nome + '\'' +
                ", cognome='" + cognome + '\'' +
                ", mat=" + mat +
                ", annoCorso=" + annoCorso +
                ", inCorso=" + inCorso +
                '}';
    }
}
