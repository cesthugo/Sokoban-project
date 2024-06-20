package modele;

public class Caisse extends Entite {


    public Caisse(Jeu _jeu, Case c) {
        super(_jeu, c);
    }

    public boolean pousser(Direction d) {
        return jeu.BougerEntite(this, d);
    }

}
