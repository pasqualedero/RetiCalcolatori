package giugno22;

import java.io.Serializable;

public class Offerta implements Serializable {
    private String settore,ruolo,tipo;
    private int ral;

    public Offerta(String settore, String ruolo, String tipo, int ral) {
        this.settore = settore;
        this.ruolo = ruolo;
        this.tipo = tipo;
        this.ral = ral;
    }

    public String getSettore() {
        return settore;
    }

    public String getTipo() {
        return tipo;
    }

    public int getRal() {
        return ral;
    }

}
