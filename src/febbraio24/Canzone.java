package febbraio24;

import java.io.Serializable;
import java.util.LinkedList;

public class Canzone implements Serializable {
    String titolo, testo;
    LinkedList<String> autori, tag;
    private int id;

    public Canzone(String titolo, String testo, LinkedList<String> autori, LinkedList<String> tag) {
        this.titolo = titolo;
        this.testo = testo;
        this.autori = autori;
        this.tag = tag;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitolo() {
        return titolo;
    }

    public LinkedList<String> getAutori() {
        return autori;
    }

    public String getTesto() {
        return testo;
    }
}
