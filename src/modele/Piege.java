package modele;

public class Piege extends Case{
    public Piege(Jeu _jeu) { super(_jeu); }

    @Override
    public boolean parcourable() {
        return e == null;
    }
}
