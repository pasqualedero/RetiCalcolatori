package luglio23;

import java.io.Serializable;
import java.util.Objects;

public class Offerta implements Serializable {
    private int prodotto, prezzo, qta;
    private Negozio negozio;

    public Negozio getNegozio() {
        return negozio;
    }
    public int getProdotto() {
        return prodotto;
    }

    public int getPrezzo() {
        return prezzo;
    }

    public int getQta() {
        return qta;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Offerta offerta = (Offerta) o;
        return prodotto == offerta.prodotto && prezzo == offerta.prezzo && qta == offerta.qta && negozio.equals(offerta.negozio);
    }

    @Override
    public int hashCode() {
        return Objects.hash(prodotto, prezzo, qta, negozio);
    }

    @Override
    public String toString() {
        return "Offerta{" +
                "prodotto=" + prodotto +
                ", prezzo=" + prezzo +
                ", qta=" + qta +
                ", negozio=" + negozio +
                '}';
    }
}
