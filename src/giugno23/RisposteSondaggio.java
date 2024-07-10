package giugno23;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;

public class RisposteSondaggio implements Serializable {
    private LinkedList<String> risposte;

    public LinkedList<String> getRisposte() {
        return risposte;
    }
}
