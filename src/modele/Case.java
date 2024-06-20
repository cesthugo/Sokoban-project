/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modele;

public abstract class Case extends Obj {

    protected Entite e;




    public boolean GoCase(Entite e) {

        setEntite(e);
        return true;
    }

    public abstract boolean parcourable();

    public void quitterLaCase() {
        e = null;
    }



    public Case(Jeu _jeu) {
        super(_jeu);
    }

    public void setEntite(Entite _e) {

        e = _e;
        e.setCase(this);}

    public Entite getEntite() {
        return e;
    }




   }
