package giugno22;

import java.net.InetAddress;

public class Record {
    private Offerta offerta;
    private boolean attiva;
    private InetAddress indirizzo;

    public Record(Offerta offerta, boolean attiva, InetAddress indirizzo) {
        this.offerta = offerta;
        this.attiva = attiva;
        this.indirizzo = indirizzo;
    }

    public void setAttiva(boolean attiva) {
        this.attiva = attiva;
    }

    public boolean isAttiva() {
        return attiva;
    }

    public InetAddress getIndirizzo() {
        return indirizzo;
    }
}
