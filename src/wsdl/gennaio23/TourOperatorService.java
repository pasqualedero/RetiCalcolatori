package wsdl.gennaio23;

import java.util.HashMap;

public class TourOperatorService {
    private HashMap<Struttura,Integer> record; //struttura, n clilenti che la occupano

    public TourOperatorService(HashMap<Struttura, Integer> record) {
        this.record = record;
    }

    public int numeroPersone(String nome){
        for (Struttura s : record.keySet()){
            if (s.equals(nome))
                return record.get(s);
        }
        return 0;
    }

    public Struttura miglioreStruttura(String citta, int stelle){
        Struttura migliore=null;
        int posti = Integer.MAX_VALUE;

        for (Struttura s : record.keySet()){
            if (s.getCitta().equals(citta) && s.getStelle()==stelle){
                if (s.getPosti()-record.get(s) < posti){
                    migliore=s;
                    posti=s.getPosti();
                }
            }
        }
        return migliore;
    }

}
