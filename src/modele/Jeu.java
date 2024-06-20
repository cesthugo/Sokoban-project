package modele;

import VueControleur.VueControleur;

import java.util.Map;
import java.util.HashMap;
import java.awt.Point;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Observable;
import java.util.Stack;
import java.util.Collections;




public class Jeu extends Observable {

    public static final int TAILLEX = 20;
    public static final int TAILLEY = 10;

    private int niveau = 1;

    private int CompteurBlocWin = 0;
    int Xi=0;
    int Yi=0;
    int Xj=0;
    int Yj=0;


    private Stack<HashMap<Entite, Point>> moveStack = new Stack<HashMap<Entite, Point>>();
    private MomentStack caretaker = new MomentStack();


    private Heros heros;

    private HashMap<Case, Point> map = new HashMap<Case, Point>();
    private Case[][] grilleEntites = new Case[TAILLEX][TAILLEY]; // permet de récupérer une case à partir de ses coordonnées


    private Menu menu;
    private WindowScore score;

//fonction permettant de demarrer un timer
    private void startTimer() {
        long startTime = System.currentTimeMillis();
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                long elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000;
                setChanged();
                notifyObservers("time");
            }
        }, 1000, 1000);
    }


    public Jeu() {
        initialisationNiveau();
        startTimer();
        menu = new Menu();

    }


    public Case[][] getGrille() {
        return grilleEntites;
    }

    public Heros getHeros() {
        return heros;
    }

    public void deplacerHeros(Direction d) {
        heros.avancerDirectionCible(d);
        setChanged();
        notifyObservers();
    }

    public void Perdu(){

        score.setState(true);
    }


    private void initialisationNiveau() {

        changementNiveau(niveau);

    }

    private void addCase(Case e, int x, int y) {
        grilleEntites[x][y] = e;
        map.put(e, new Point(x, y));
    }

    private Case returnCase (int x, int y){

        return grilleEntites[x][y];
    }

    private Memento createMemento() {
        HashMap<Point, Case> state = new HashMap<>();
        HashMap<Point, Entite> entiteState = new HashMap<>();
        for (int x = 0; x < TAILLEX; x++) {
            for (int y = 0; y < TAILLEY; y++) {
                state.put(new Point(x, y), grilleEntites[x][y]);
                if (grilleEntites[x][y].getEntite() != null) {
                    entiteState.put(new Point(x, y), grilleEntites[x][y].getEntite());
                }
            }
        }
        return new Memento(state, entiteState);
    }


    public void undo() {
        Memento memento = caretaker.undo();
        if (memento != null) {
            restoreFromMemento(memento);
            setChanged();
            notifyObservers();
        }
    }

    public void redo() {
        Memento memento = caretaker.redo();
        if (memento != null) {
            restoreFromMemento(memento);
            setChanged();
            notifyObservers();
        }
    }

    private void restoreFromMemento(Memento memento) {
        /*for (Map.Entry<Point, Case> entry : memento.getState().entrySet()) {
            Point p = entry.getKey();
            grilleEntites[p.x][p.y] = entry.getValue(); // Restaure la case
            map.put(entry.getValue(), p); // Met à jour la HashMap map
        }*/

        for (Map.Entry<Point, Entite> entry : memento.getEntiteState().entrySet()) {
            Point p = entry.getKey();
            Entite entiteCourante = entry.getValue();

            if (p != null)
            {
                entiteCourante.getCase().quitterLaCase();
                caseALaPosition(p).GoCase(entiteCourante);
            }


            /*grilleEntites[p.x][p.y].setEntite(entry.getValue()); // Restaure l'entité dans la case
            entry.getValue().setCase(grilleEntites[p.x][p.y]); // Met à jour la référence de l'entité à sa case*/
        }
        setChanged();
        notifyObservers();
    }




    public boolean BougerEntite(Entite e, Direction d) {
        boolean isMoved = true;

        Point posActuelle = map.get(e.getCase());
        Point point_Destination = calculerPointCible(posActuelle, d);

        caretaker.saveState(createMemento()); // Sauvegarde de l'état actuel
        Case destCase = caseALaPosition(point_Destination);
        Case CurrentCase = caseALaPosition(posActuelle);

        if (PositionEstValide(point_Destination)) {
            Entite entiteCible = caseALaPosition(point_Destination).getEntite();
            if (entiteCible != null) {
                entiteCible.pousser(d);
            }

            // Traitement des interactions avec les portails
                if (destCase instanceof Portal) {
                    Portal portalDest = ((Portal) destCase).getDestinationPortal();
                    if (portalDest != null) {
                        e.getCase().quitterLaCase();
                        portalDest.GoCase(e);
                        BougerEntite(e, d); // Récursion gérée ici
                    }

                } else {
                    // Gestion des déplacements standards
                    if (destCase.parcourable()) {
                        if ((destCase instanceof RailHB) &&  (d == Direction.gauche || d == Direction.droite))
                        {
                            isMoved = false; //on ne peut pas acceder a des rails
                        }else
                       if( (CurrentCase instanceof RailHB) &&  (d == Direction.gauche || d == Direction.droite) ){
                            isMoved = false; //si on est sur une horizonta on peut se deplacer seulement par la droite ou la gauche
                        }else
                        if( (CurrentCase instanceof RailGD) &&  (d == Direction.haut || d == Direction.bas) ){
                            isMoved = false; //si on est sur une horizonta on peut se deplacer seulement par la droite ou la gauche
                        }else {
                            if ((destCase instanceof RailGD) &&  (d == Direction.haut || d == Direction.bas))
                            {
                                isMoved = false; //on ne peut pas acceder a des rails
                            }else
                            {

                                e.getCase().quitterLaCase();
                                destCase.GoCase(e);

                                // Suivi du mouvement pour possible annulation
                                HashMap<Entite, Point> newPosMap = new HashMap<>();
                                newPosMap.put(e, posActuelle);
                                moveStack.push(newPosMap);

                                if (destCase instanceof Glace) {
                                    BougerEntite(e, d); // Gestion de la glace
                                }
                            }
                        }
                    } else {
                        isMoved = false; // Indique un échec de déplacement
                    }
                }
            } else {
                isMoved = false; // Déplacement impossible
            }



            return isMoved;
    }


    public boolean A_Perdu()
    {
        return score.getState();

    }

    public void mettreJeuEnPause() {
        menu.setState(false);
    }

    public void continuerJeu() {
        menu.setState(true);
    }

    public boolean EnPause() {
        return (menu.getState() == false);
    }

    public int getNiveau() {
        return niveau;
    }

    public void setNiveau(int _niveau) {
        niveau = _niveau;
    }

    public boolean LvlEnd() {
        int compteur = 0;
        for (int x = 0; x < TAILLEX; x++) {
            for (int y = 0; y < TAILLEY; y++) {
                if (this.getGrille()[x][y] instanceof Target && this.getGrille()[x][y].getEntite() instanceof Caisse) {
                    compteur++;
                }
            }
        }
        return compteur == CompteurBlocWin;
    }

    public boolean estSurPiege() {
        for (int x = 0; x < TAILLEX; x++) {
            for (int y = 0; y < TAILLEY; y++) {
                if (grilleEntites[x][y] instanceof Piege && grilleEntites[x][y].getEntite() instanceof Heros) {
                    return true;
                }
            }
        }
        return false;
    }

    public void changementNiveau(int _niveau) {
        int tmpX = 0;
        int tmpY = 0;
        CompteurBlocWin= 0;

        StringBuffer s = new StringBuffer();
        try {
            File file = new File("Lvl" + String.valueOf(_niveau) + ".txt");
            VueControleur.Bruit("src/son/clic.wav");

            FileReader fr = new FileReader(file);

            BufferedReader buffer = new BufferedReader(fr);
            String line;

            tmpX = Integer.parseInt(buffer.readLine());
            tmpY = Integer.parseInt(buffer.readLine());

            while ((line = buffer.readLine()) != null) {
                System.out.println(line);
                s.append(line);
            }
            System.out.println("La taille x est : " + tmpX + " la taille y est : " + tmpY);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Point pointS = null;  // Coordonnées pour le portail 'S'
        Point pointK = null;  // Coordonnées pour le portail 'K'

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            int x = i % tmpX; // Coordonnée X de la case
            int y = (int) (i / tmpX); // Coordonnée Y de la case

            switch (c) {
                case 'M':
                    addCase(new Mur(this), x, y);
                    break;
                case '_':
                    addCase(new Vide(this), x, y);
                    break;
                case 'T':
                    addCase(new Target(this), x, y);
                    break;
                case 'P':
                    addCase(new Piege(this), x, y);
                    break;
                case 'H':
                    addCase(new Vide(this), x, y);
                    heros = new Heros(this, grilleEntites[x][y]);
                    break;
                case 'B':
                    addCase(new Vide(this), x, y);
                    Caisse b = new Caisse(this, grilleEntites[x][y]);
                    CompteurBlocWin++;
                    break;
                case 'G':
                    addCase(new Glace(this), x, y);
                    break;
                case 'S':
                    addCase(new Portal(this), x, y);
                    pointS = new Point(x, y);  // Sauvegardez la position de 'S'
                    break;
                case 'K':
                    addCase(new Portal(this), x, y);
                    pointK = new Point(x, y);  // Sauvegardez la position de 'K'
                    break;
                case 'R': //rail horizontale
                    addCase(new RailGD(this), x, y);
                    break;
                case 'V': //rail verticale-
                    addCase(new RailHB(this), x, y);
                    break;
                default:
                    break;
            }
        }

// Après avoir lu tous les caractères et trouvé les portails
        if (pointS != null && pointK != null) {
            Portal portalS = (Portal) returnCase(pointS.x, pointS.y);
            Portal portalK = (Portal) returnCase(pointK.x, pointK.y);
            if (portalS != null && portalK != null) {
                portalS.setDestinationPortal(portalK);  // Connectez S à K
                portalK.setDestinationPortal(portalS);  // Connectez K à S si nécessaire
            }
        }

    }




    private Point calculerPointCible(Point pCourant, Direction d) {
        Point pCible = null;

        switch (d) {
            case haut:
                pCible = new Point(pCourant.x, pCourant.y - 1);
                break;
            case bas:
                pCible = new Point(pCourant.x, pCourant.y + 1);
                break;
            case gauche:
                pCible = new Point(pCourant.x - 1, pCourant.y);
                break;
            case droite:
                pCible = new Point(pCourant.x + 1, pCourant.y);
                break;

        }

        return pCible;
    }



    private boolean PositionEstValide(Point p) {
        return p.x >= 0 && p.x < TAILLEX && p.y >= 0 && p.y < TAILLEY;
    }

    private Case caseALaPosition(Point p) {
        Case retour = null;

        if (PositionEstValide(p)) {
            retour = grilleEntites[p.x][p.y];
        }

        return retour;
    }

}