package wsdl.luglio23;

import java.util.HashMap;
import java.util.LinkedList;

public class UpdatePrezzo {

    private HashMap<String, LinkedList<String>> provincie; //provincia -> lista(iva)
    private HashMap<String, LinkedList<PrezzoProdotto>> grossisti; //iva -> lista(prodotti)

    public UpdatePrezzo(){
        provincie = new HashMap<String, LinkedList<String>>();
        grossisti = new HashMap<String, LinkedList<PrezzoProdotto>>();
    }

    public void updatePrezzo(PrezzoProdotto p){
        for (String iva : grossisti.keySet()){
            if (iva==p.getPartitaIva()){
                LinkedList<PrezzoProdotto> prodotti = grossisti.get(iva);
                for (PrezzoProdotto prodotto : prodotti){
                    if (p.getNomeOrtaggio().equals(prodotto.getNomeOrtaggio()))
                        prodotto.setPrezzo(p.getPrezzo());
                }
            }
        }
    }

    public String minPrezzoMedio(String ortaggio){
        String provM="";
        double prezzoM=Double.MAX_VALUE;

        for (String provincia: provincie.keySet()){
            int n=0; double prezzo=0;
            LinkedList<String> ive = provincie.get(provincia);
            for (String iva : ive){
                LinkedList<PrezzoProdotto> prodotti = grossisti.get(iva);
                for (PrezzoProdotto p : prodotti){
                    if (p.getNomeOrtaggio().equals(ortaggio)){
                        n++; prezzo+=p.getPrezzo();
                    }
                }
            }
            if (prezzo < prezzoM){
                prezzoM=prezzo; provM=provincia;
            }
        }
        return provM;
    }

}
