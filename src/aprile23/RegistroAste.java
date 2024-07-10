package aprile23;

import java.net.InetAddress;
import java.util.HashMap;

public class RegistroAste {
    private HashMap<Integer,Record> registro = new HashMap<>(); // nel Record mi salvo man mano la migliore offerta e il
                                                                // miglior offerente

    //costruttore di default

    public synchronized void setApertura(boolean valore, int idAsta){
        Record r = registro.get(idAsta);
        r.setAstaAperta(valore);
    }

    public InetAddress getCliente(int idAsta){
        return registro.get(idAsta).getIdCliente();
    }

    public synchronized void addOfferta(Offerta candidata, InetAddress cliente){
        if (!registro.containsKey(candidata.getIdAsta()))
            registro.put(candidata.getIdAsta(), new Record(cliente, candidata));
        Offerta migliore = registro.get(candidata.getIdAsta()).getMiglioreOfferta();
        Record record = registro.get(candidata.getIdAsta());
        if (candidata.getImporto() > migliore.getImporto()){
            record.setMiglioreOfferta(candidata);
            record.setIdCliente(cliente);
        }
    }

    public boolean astaAperta(int idAsta){
        return registro.get(idAsta).isAstaAperta();
    }

}
