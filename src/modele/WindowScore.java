package modele;

public class WindowScore {
    private boolean state;

    public WindowScore()
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
