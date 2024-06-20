package modele;

public class Target extends Case{

    public Target(Jeu _jeu) { super(_jeu); }

    @Override
    public boolean parcourable() {
        return e == null;
    }

}


