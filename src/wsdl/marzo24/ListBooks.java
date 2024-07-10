package wsdl.marzo24;

import java.io.Serializable;
import java.util.LinkedList;

public class ListBooks implements Serializable {
    private LinkedList<Book> libri;

    public ListBooks(LinkedList<Book> libri) {
        this.libri = libri;
    }

    public void add(Book b){
        libri.add(b);
    }
}
