package giugno23;

public class Record {
    private Sondaggio sondaggio;
    private boolean aperto;

    public Record(Sondaggio sondaggio) {
        this.sondaggio = sondaggio;
        this.aperto = true;
    }

    public void setAperto(boolean aperto) {
        this.aperto = aperto;
    }

    public boolean isAperto() {
        return aperto;
    }

    public Sondaggio getSondaggio() {
        return sondaggio;
    }
}
