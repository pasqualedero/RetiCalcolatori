package wsdl.aprile24;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class AziendaService {
    private HashMap<Integer, HashSet<IncassoProdotto>> map;

    public AziendaService(HashMap<Integer, HashSet<IncassoProdotto>> map) {
        this.map = map;
    }

    public void vendita(int idAzienda, String nomeVino, int quantita, double importo){
        map.get(idAzienda).add(new IncassoProdotto(nomeVino,idAzienda,quantita,importo));
    }

    public IncassoProdotto maggioreIncasso(int idAzienda){
        IncassoProdotto incasso = null;
        double best = -1;
        HashSet<IncassoProdotto> set = map.get(idAzienda);
        for (IncassoProdotto i : set){
            double tot = i.getQuantita()*i.getImporto();
            if (tot>best){
                incasso=i;
                best=tot;
            }
        }
        return incasso;
    }

}
