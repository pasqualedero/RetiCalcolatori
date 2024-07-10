package wsdl.febbraio23;

import java.util.HashMap;
import java.util.LinkedList;

public class LibrerieService {

    private HashMap<String, LinkedList<HashMap<String,LinkedList<Integer>>>> mappa; //iva,lista (isbn(giorni,n_vendite))
    private LinkedList<Libreria> librerie;
    public LibrerieService(HashMap<String, LinkedList<HashMap<String,LinkedList<Integer>>>> mappa, LinkedList<Libreria> librerie) {
        this.mappa = mappa;
        this.librerie=librerie;
    }

    public int venditeISBN(String iva, String isbn){
        LinkedList<HashMap<String,LinkedList<Integer>>> l = mappa.get(iva);
        for (HashMap<String,LinkedList<Integer>> map : l){
            for (String cod : map.keySet()){
                if (cod.equals(isbn) && map.get(cod).get(0)<=30)
                    return map.get(cod).get(1);
            }
        }
        return 0;
    }

    public Libreria venditeCategoria(String categoria){
        return null; //TODO
    }

}
