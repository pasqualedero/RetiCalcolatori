package wsdl.marzo24;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.StringTokenizer;

public class BookService {

    private HashMap<String, LinkedList<Book>> registro; // autore -> lista(libri)

    public BookService(HashMap<String, LinkedList<Book>> registro) {
        this.registro = registro;
    }

    public ListBooks searchBook(String query){
        ListBooks result = new ListBooks(new LinkedList<Book>());

        StringTokenizer st = new StringTokenizer(query," -");
        String aut = st.nextToken();
        String gen = st.nextToken();
        double pre = Double.parseDouble(st.nextToken());

        LinkedList<Book> libri = registro.get(aut);

        for (Book b : libri){
            if (b.getGenere().equals(gen) && b.getPrezzo()<pre){
                result.add(b);
            }
        }
        return result;
    }

    public boolean addBook(Book book){
        boolean success=false;

        String aut = book.getAutore();
        LinkedList<Book> libri = registro.get(aut);
        if (libri.size()==10) return success;
        for (Book libro : libri){
            if (libro.getTitolo().equals(book.getTitolo()))
                return success;
        }
        success=true;
        return success;
    }



}
