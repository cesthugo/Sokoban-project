package modele;

public class Vide extends Case {

    public Vide(Jeu _jeu) { super(_jeu); }

    @Override
    public boolean parcourable() {
        return e == null;
    }



}
