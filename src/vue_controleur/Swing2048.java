package vue_controleur;

import modele.Case;
import modele.Direction;
import modele.Jeu;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;


/* Là où on va mettre en place le fonctionnement de la Fenêtre. */
public class Swing2048 extends JFrame implements Observer {
    private static final int PIXEL_PER_SQUARE = 60;
    // tableau de cases : i, j -> case graphique
    private JLabel[][] tabC1, tabC2; //On déclare deux tableaux prochainement identiques pour créer les deux JPanels
    private Jeu jeu;
    boolean initializer;

    public Swing2048(Jeu _jeu) {
        jeu = _jeu;

        //initializer = true;

        // Fonction qui s'occupe de terminer la fenêtre lorsque l'on appuie sur un bouton fermer.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Définie la taille de la JFrame en fonction de la taille de la fenêtre ().
        setSize(jeu.getSize() * PIXEL_PER_SQUARE * 2 + 100, jeu.getSize() * PIXEL_PER_SQUARE);
        //pack();

        //setLocation(_x, _y);

        // On récupère le panel par défaut et on change sa définition
        JPanel panel = (JPanel)this.getContentPane();
        // On le présente sous la forme d'un tableau 1, 2
        panel.setLayout(new GridLayout(1, 2));

        // JLabel : Composant permettant d'afficher du texte ou une image.
        // On alloue dans la mémoire un tableau 2D de Type JLabel.
        // On applique les mêmes traitements aux deux tableau sémantiquement différents
        tabC1 = new JLabel[jeu.getSize()][jeu.getSize()];
        tabC2 = new JLabel[jeu.getSize()][jeu.getSize()];

        // Un Pane est associé à un JFrame, ce dernier est soit un contentPane soit un menuPane.
        // Un pane sera a son tour affecté à un Layout Manager pour dire comment le Pane se comporte dans la JFrame.
        JPanel panel1 = new JPanel(new GridLayout(jeu.getSize(), jeu.getSize()));
        JPanel panel2 = new JPanel(new GridLayout(jeu.getSize(), jeu.getSize()));
        //JPanel panel3 = new JPanel(new GridLayout(jeu.getSize(), jeu.getSize()));
        // Défini le placement des fils en gridLayout.

        //panel1.setLocation(0, 0);
        //panel2.setLocation(100, 0);

        for (int i = 0; i < jeu.getSize(); i++) {
            for (int j = 0; j < jeu.getSize(); j++) {
                // On créer un objet Bordure en utilisant l'interface Border, ici on utilisera la méthode createLineBorder pour avoir un LineBorder. 
                // Factory : Design patern qui permet de créer un nouvelle objet à partir d'une interface (ici BorderFactory)
                Border border = BorderFactory.createLineBorder(Color.darkGray, 5);
                // Dans chaque case du tableau on stock on composant JLabel.
                tabC1[i][j] = new JLabel();
                tabC2[i][j] = new JLabel();
                // On défini pour ce composant une bordure avec setBorder.
                tabC1[i][j].setBorder(border);
                tabC2[i][j].setBorder(border);
                // On définie l'alignement du composant pour qu'il soit au centre. 
                tabC1[i][j].setHorizontalAlignment(SwingConstants.CENTER);
                tabC2[i][j].setHorizontalAlignment(SwingConstants.CENTER);


                // On affecte respectivement les tableaux différents aux Panels différents
                panel1.add(tabC1[i][j]);
                panel2.add(tabC2[i][j]);
                //panel3.add(tabC[i][j]);

            }
        }
        // On remplace le ContentPane par notre Panel pane que l'on vient de créer.
        setContentPane(panel);
        panel.add(panel1);
        panel.add(panel2);
        //panel.add(panel3);
        //setContentPane(panel);
        ajouterEcouteurClavier();
        // Récupère les informations dans le tableau Jeu et met dans les labels du texte.
        rafraichir();
        //initializer = false;
    }

    /**
     * Correspond à la fonctionnalité de Vue : affiche les données du modèle
     */
    private void rafraichir()  {

        // On passe en paramètre de InvokeLater une classe anonyme.
        SwingUtilities.invokeLater(new Runnable() { // demande au processus graphique de réaliser le traitement
            @Override
            public void run() {
                for (int i = 0; i < jeu.getSize(); i++) {
                    for (int j = 0; j < jeu.getSize(); j++) {
                        Case c = jeu.getCase(i, j);
                        // Si la case du tableau est null on affiche une case avec un texte vide.
                        if (c == null || c.getValeur() == 0) {

                            tabC1[i][j].setText("");
                            tabC2[i][j].setText("");

                        } else {
                            tabC1[i][j].setText(c.getValeur() + "");
                            tabC2[i][j].setText(c.getValeur() + ""); // On lui met une valeur 2 , 4 ....
                        }


                    }
                }
            }
        });


    }

    /**
     * Correspond à la fonctionnalité de Contrôleur : écoute les évènements, et déclenche des traitements sur le modèle
     */
    private void ajouterEcouteurClavier() {
        // For testing.
        /*for(int i = 0; i < 4; i++) {
            jeu.setTabCases(1,i,2);
        }*/
        //jeu.ajoute_nombre_aleatoire();
        jeu.ajoute_nombre_aleatoire();
        addKeyListener(new KeyAdapter() { // new KeyAdapter() { ... } est une instance de classe anonyme, il s'agit d'un objet qui correspond au controleur dans MVC
            @Override
            public void keyPressed(KeyEvent e) {
                switch(e.getKeyCode()) {  // on regarde quelle touche a été pressée
                    case KeyEvent.VK_LEFT : jeu.monTest(Direction.gauche); break; // A changer, car ici à chaque case que l'on appuie on génère une nouvelle classe.
                    case KeyEvent.VK_RIGHT : jeu.monTest(Direction.droite); break;
                    case KeyEvent.VK_DOWN : jeu.monTest(Direction.bas); break;
                    case KeyEvent.VK_UP : jeu.monTest(Direction.haut); break;
                }
            }
        });
    }


    @Override
    public void update(Observable o, Object arg) {
        rafraichir();
    }
}