package modele;

public class Portal extends Case {
    private Portal destinationPortal; // Pointe vers le portail jumeau

    public Portal(Jeu _jeu) {
        super(_jeu);
    }

    public boolean parcourable() {
        // Un portail peut toujours être traversé
        return true;
    }

    // Méthode pour définir le portail de destination
    public void setDestinationPortal(Portal destinationPortal) {
        this.destinationPortal = destinationPortal;
    }

    // Méthode pour récupérer le portail de destination
    public Portal getDestinationPortal() {
        return destinationPortal;
    }
}
