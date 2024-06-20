package modele;

public class Menu {
    private boolean state; // etat du jeu pause ou en marche

    public Menu()
    {
        state = false;
    }

    public boolean getState()
    {
        return state;
    }

    public void setState(boolean _state)
    {
        state = _state;
    }


}
