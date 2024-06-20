package modele;

public class Glace extends Case {
    public Glace(Jeu _jeu) { super(_jeu); }


    @Override
    public boolean parcourable() {
        return true;
    }
}
