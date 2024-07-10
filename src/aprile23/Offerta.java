package aprile23;

import javax.imageio.plugins.jpeg.JPEGImageReadParam;
import java.io.Serializable;

public class Offerta implements Serializable {
    private String cf;
    private int idAsta;
    private int importo;

    public Offerta(String cf, int idAsta, int importo) {
        this.cf = cf;
        this.idAsta = idAsta;
        this.importo = importo;
    }

    public int getImporto(){
        return importo;
    }

    public String getCf() {
        return cf;
    }

    public int getIdAsta() {
        return idAsta;
    }

    @Override
    public String toString() {
        return "Offerta{" +
                "cf='" + cf + '\'' +
                ", idAsta=" + idAsta +
                ", importo=" + importo +
                '}';
    }
}
