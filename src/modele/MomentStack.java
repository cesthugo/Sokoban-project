package modele;
import java.util.Stack;

public class MomentStack {
    private Stack<Memento> undoStack = new Stack<>();
    private Stack<Memento> redoStack = new Stack<>();

    public void saveState(Memento memento) {
        undoStack.push(memento);
        redoStack.clear();  // Permet de reintialiser la pile redo a chaque sauvegarde du jeu
    }

    public Memento redo() {//permet de recuperer le moment d'après
        if (!redoStack.isEmpty()) {
            Memento memento = redoStack.pop();
            undoStack.push(memento);
            return memento;
        }
        return null;
    }

    public Memento undo() {// permet de recuprer le moment d'avant
        if (!undoStack.isEmpty()) {
            Memento memento = undoStack.pop();
            redoStack.push(memento);
            return memento;
        }
        return null;
    }


}