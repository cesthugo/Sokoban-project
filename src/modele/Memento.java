package modele;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

public class Memento {
    private Map<Point, Case> state;
    private Map<Point, Entite> entiteState;

    public Memento(Map<Point, Case> state, Map<Point, Entite> entiteState) {
        this.state = new HashMap<>(state);  // Copie profonde de l'état des cases
        this.entiteState = new HashMap<>(entiteState);  // Copie profonde de l'état des entités
    }

    public Map<Point, Case> getState() {
        return state;
    }

    public Map<Point, Entite> getEntiteState() {
        return entiteState;
    }
}
