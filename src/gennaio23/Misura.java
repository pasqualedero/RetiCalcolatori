package gennaio23;

import java.io.Serializable;
import java.util.Date;

public record Misura(int idSensore, double valore, Date timestamp) implements Serializable {
}
