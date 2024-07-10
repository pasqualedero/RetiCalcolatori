package aprile23;

import java.net.InetAddress;

public class Record {
    private InetAddress idCliente;
    private Offerta miglioreOfferta;
    private boolean astaAperta;

    public Record(InetAddress idCliente, Offerta offerta) {
        this.idCliente = idCliente;
        this.miglioreOfferta = offerta;
        this.astaAperta = true;
    }

    public void setAstaAperta(boolean astaAperta) {
        this.astaAperta = astaAperta;
    }

    public void setIdCliente(InetAddress idCliente) {
        this.idCliente = idCliente;
    }

    public void setMiglioreOfferta(Offerta miglioreOfferta) {
        this.miglioreOfferta = miglioreOfferta;
    }

    public InetAddress getIdCliente() {
        return idCliente;
    }

    public Offerta getMiglioreOfferta() {
        return miglioreOfferta;
    }

    public boolean isAstaAperta() {
        return astaAperta;
    }
}
