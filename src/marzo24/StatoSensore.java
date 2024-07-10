package marzo24;

import java.io.Serializable;

public class StatoSensore implements Serializable {
    private int id, numProg;
    private int aria,suolo;

    public StatoSensore(int id, int aria, int suolo) {
        this.id = id;
        this.aria = aria;
        this.suolo = suolo;
    }

    public void setNumProg(int numProg) {
        this.numProg = numProg;
    }

    public int getId() {
        return id;
    }

    public int getNumProg() {
        return numProg;
    }

    public int getAria() {
        return aria;
    }

    public int getSuolo() {
        return suolo;
    }
}
