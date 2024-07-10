package giugno23;

import java.io.Serializable;
import java.util.LinkedList;

public class Sondaggio implements Serializable {
    private int id;
    private String nome;
    private LinkedList<String> listaDomande;

    public Sondaggio(int id, String nome, LinkedList<String> listaDomande) {
        this.id = id;
        this.nome = nome.substring(0,200); //lunghezza max 200
        this.listaDomande = listaDomande;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public LinkedList<String> getListaDomande() {
        return listaDomande;
    }
}
