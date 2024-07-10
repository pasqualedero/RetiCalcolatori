package wsdl.marzo24;

import java.io.Serializable;

public class Book implements Serializable {
    private String isbn, titolo, autore, genere;
    private double prezzo;

    public Book(String isbn, String titolo, String autore, String genere, double prezzo) {
        this.isbn = isbn;
        this.titolo = titolo;
        this.autore = autore;
        this.genere = genere;
        this.prezzo = prezzo;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitolo() {
        return titolo;
    }

    public String getAutore() {
        return autore;
    }

    public String getGenere() {
        return genere;
    }

    public double getPrezzo() {
        return prezzo;
    }
}
