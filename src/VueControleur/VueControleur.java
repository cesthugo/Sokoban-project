package VueControleur;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Flow;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.sound.sampled.*;


import modele.*;


/** Cette classe a deux fonctions :
 *  (1) Vue : proposer une représentation graphique de l'application (cases graphiques, etc.)
 *  (2) Controleur : écouter les évènements clavier et déclencher le traitement adapté sur le modèle (flèches direction Pacman, etc.))
 *
 */
public class VueControleur extends JFrame implements Observer {


    private ImageIcon iconePortal;
    private ImageIcon iconePortal2;
    private ImageIcon iconeHero;
    private ImageIcon iconeVide;
    private ImageIcon iconeMur;
    private ImageIcon iconeBloc;
    private ImageIcon iconeTarget;
    private ImageIcon iconePiege;
    private ImageIcon iconeGlace;
    private ImageIcon iconeRailHB;
    private ImageIcon iconeRailGD;



    private Jeu jeu;
    private int sizeX;
    private int sizeY;

    private Map<Class<?>, ImageIcon> iconMap;



    private ImageIcon iconeMenu;



    private JLabel[][] tabJLabel;
    private JLabel menuJLabel;

    public static void Music(String filePath) { //permet de jouer la musique en boucle
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filePath));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.loop(0);
        } catch (Exception ex) {
            System.out.println("Une erreur s'est produite pour la musique : " + ex.getMessage());
        }
    }

    public static void Bruit(String filePath) {// permet de jouer le son
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filePath));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception ex) {
            System.out.println("Une erreur s'est produite pour le bruit : " + ex.getMessage());
        }
    }
    public VueControleur(Jeu _jeu) {

        sizeX = _jeu.TAILLEX;
        sizeY = _jeu.TAILLEY;
        jeu = _jeu;

        SetIcons();
       InitialiserGraphique();
        ToucheSelect();

        jeu.addObserver(this);

        AffichageUpdate();
        Music("src/son/SokoSong.wav");
    }

    private void ToucheSelect() {
        addKeyListener(new KeyAdapter() { // new KeyAdapter() { ... } est une instance de classe anonyme, il s'agit d'un objet qui correspond au controleur dans MVC
            @Override
            public void keyPressed(KeyEvent e) {

                if (jeu.EnPause())
                {
                    if(e.getKeyCode() == KeyEvent.VK_ENTER) {  // on regarde quelle touche a été pressée lorsque le jeu est en pause.
                        jeu.continuerJeu(); AffichageUpdate();
                    }
                    else if (e.getKeyCode() == KeyEvent.VK_R) // Restart
                    {
                        jeu.changementNiveau(jeu.getNiveau());
                    jeu.continuerJeu(); AffichageUpdate();
                    }
                    else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) // Leave
                    {
                        System.exit(0);
                    }
                }

                else
                {
                    switch(e.getKeyCode()) {

                        case KeyEvent.VK_LEFT : jeu.deplacerHeros(Direction.gauche); break;
                        case KeyEvent.VK_RIGHT : jeu.deplacerHeros(Direction.droite); break;
                        case KeyEvent.VK_DOWN : jeu.deplacerHeros(Direction.bas); break;
                        case KeyEvent.VK_UP : jeu.deplacerHeros(Direction.haut); break;
                        case KeyEvent.VK_P : jeu.mettreJeuEnPause(); AffichageUpdate(); break; // PAUSE
                        case KeyEvent.VK_U : System.out.println("UNDO"); jeu.undo(); AffichageUpdate(); break; // UNDO
                        case KeyEvent.VK_K : System.out.println("REDO"); jeu.redo(); AffichageUpdate(); break; // REDO

                    }
                }
            }
        });
    }

    private ImageIcon  redimensionnerIcone(ImageIcon icon, int width, int height) {
        if (icon != null) {

            Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH); //permet de redimensionner l'image
            return new ImageIcon(img);
        }
        return null;
    }


    private void SetIcons() { //permet de charger les images pour chaque entitées et éléments du jeu


        iconePiege = MettreImageIcone("Images/piege.png");
        iconeGlace = MettreImageIcone("Images/Glace.png");
        iconePortal = MettreImageIcone("Images/Portal.png");
        iconePortal2 = MettreImageIcone("Images/Portal.png");
        iconeHero = MettreImageIcone("Images/Pousseur.png");
        iconeVide = MettreImageIcone("Images/Sol.png");
        iconeMur = MettreImageIcone("Images/Mur.png");
        iconeBloc = MettreImageIcone("Images/Caisse.png");
        iconeTarget = MettreImageIcone("Images/But.png");
        iconeRailHB = MettreImageIcone("Images/RailHB.png");
        iconeRailGD = MettreImageIcone("Images/RailGD.png");

        iconeMur = redimensionnerIcone(iconeMur, 1000 / sizeX, 750 / sizeY);
        iconeVide = redimensionnerIcone(iconeVide, 1000 / sizeX, 750 / sizeY);
        iconeBloc = redimensionnerIcone(iconeBloc, 1000 / sizeX, 750 / sizeY);
        iconeTarget = redimensionnerIcone(iconeTarget, 1000 / sizeX, 750 / sizeY);
        iconeHero = redimensionnerIcone(iconeHero, 1000 / sizeX, 750 / sizeY);
        iconePiege = redimensionnerIcone(iconePiege, 1000 / sizeX, 750 / sizeY);
        iconeGlace = redimensionnerIcone(iconeGlace, 1000 / sizeX, 750 / sizeY);
        iconePortal = redimensionnerIcone(iconePortal, 1000 / sizeX, 750 / sizeY);
        iconePortal2 = redimensionnerIcone(iconePortal2, 1000 / sizeX, 750 / sizeY);
        iconeRailGD = redimensionnerIcone(iconeRailGD, 1000 / sizeX, 750 / sizeY);
        iconeRailHB = redimensionnerIcone(iconeRailHB, 1000 / sizeX, 750 / sizeY);



        iconeMenu = MettreImageIcone("Images/Acceuil.png");
    }

    private ImageIcon MettreImageIcone(String urlIcone) {
        BufferedImage image = null;

        try {
            image = ImageIO.read(new File(urlIcone));
        } catch (IOException ex) {
            Logger.getLogger(VueControleur.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

        return new ImageIcon(image);
    }

    private void InitialiserGraphique() {

        setTitle("Sokoban");
        setSize(1000, 750); // dimension  fenêtre
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // permet de sortir du jeu

        JComponent jlabGrille = new JPanel(new GridLayout(sizeY, sizeX));

        tabJLabel = new JLabel[sizeX][sizeY];
        menuJLabel = new JLabel();


        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                JLabel jlab = new JLabel();
                tabJLabel[x][y] = jlab;
                jlabGrille.add(jlab);
            }
        }
        add(jlabGrille);
    }




    private void initialiserIconMap() {
        iconMap = new HashMap<>();
        iconMap.put(Heros.class, iconeHero);
        iconMap.put(Caisse.class, iconeBloc);
        iconMap.put(Mur.class, iconeMur);
        iconMap.put(Vide.class, iconeVide);
        iconMap.put(Target.class, iconeTarget);
        iconMap.put(Piege.class, iconePiege);
        iconMap.put(Glace.class, iconeGlace);
        iconMap.put(Portal.class, iconePortal);
        iconMap.put(RailHB.class, iconeRailHB);
        iconMap.put(RailGD.class, iconeRailGD);
    }

    private void mettreAJourIcones() {
        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                Case c = jeu.getGrille()[x][y];
                Entite e = c.getEntite();
                Class<?> classe = (e != null ? e.getClass() : c.getClass());
                ImageIcon icone = iconMap.get(classe);

                if (icone != null) {
                    tabJLabel[x][y].setIcon(icone);
                }
            }
        }
    }





    private void AffichageUpdate() {
        if (jeu.EnPause())
        {
            getContentPane().removeAll();
            Image iconeMenuResize = iconeMenu.getImage().getScaledInstance(1000, 750, Image.SCALE_SMOOTH);
            iconeMenu = new ImageIcon(iconeMenuResize);
            menuJLabel.setIcon(iconeMenu);
            add(menuJLabel);

            System.out.println("Le jeu est en pause.");
        }
        else
        {
            getContentPane().removeAll();

            JComponent grilleJLabels = new JPanel(new GridLayout(sizeY, sizeX)); // grilleJLabels va contenir les cases graphiques et les positionner sous la forme d'une grille

            tabJLabel = new JLabel[sizeX][sizeY];


            for (int y = 0; y < sizeY; y++) {
                for (int x = 0; x < sizeX; x++) {
                    JLabel jlab = new JLabel();
                    tabJLabel[x][y] = jlab; // on conserve les cases graphiques dans tabJLabel pour avoir un accès pratique à celles-ci (voir mettreAJourAffichage() )
                    grilleJLabels.add(jlab);
                }
            }
            add(grilleJLabels);

            initialiserIconMap();
            mettreAJourIcones();



        }

        revalidate();
        repaint();
    }



    @Override
    public void update(Observable o, Object arg) {
        AffichageUpdate();
        if (jeu.estSurPiege())
        {
            jeu.changementNiveau(1);
            jeu.setNiveau(1);
            jeu.mettreJeuEnPause();
            AffichageUpdate();
        }
        if (jeu.LvlEnd())
        {
            if (jeu.getNiveau() == 5)
            {


                jeu.changementNiveau(1);
                jeu.setNiveau(1);
                jeu.mettreJeuEnPause();
                AffichageUpdate();


            }
            jeu.changementNiveau(jeu.getNiveau() + 1);
            jeu.setNiveau(jeu.getNiveau() + 1);
            AffichageUpdate();
        }

    }


}
