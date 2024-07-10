package febbraio23;

import java.util.Calendar;

public class Concorso {
    private int id, posti;
    private Calendar dataScadenza;

    public Concorso(int id, int posti, Calendar dataScadenza) {
        this.id = id;
        this.posti = posti;
        this.dataScadenza = dataScadenza;
    }

    public int getId() {
        return id;
    }

    public int getPosti() {
        return posti;
    }

    public Calendar getDataScadenza() {
        return dataScadenza;
    }
}
