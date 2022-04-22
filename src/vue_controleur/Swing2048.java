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
    private JLabel[][] tabC;
    private Jeu jeu;


    public Swing2048(Jeu _jeu) {
        jeu = _jeu;
        // Fonction qui s'occupe de terminer la fenêtre lorsque l'on appuie sur un bouton fermer.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Définie la taille de la JFrame en fonction de la taille de la fenêtre ().
        setSize(jeu.getSize() * PIXEL_PER_SQUARE, jeu.getSize() * PIXEL_PER_SQUARE);

        // JLabel : Composant permettant d'afficher du texte ou une image.
        // On alloue dans la mémoire un tableau 2D de Type JLabel.
        tabC = new JLabel[jeu.getSize()][jeu.getSize()];

        // Un Pane est associé à un JFrame, ce dernier est soit un contentPane soit un menuPane.
        // Un pane sera a son tour affecté à un Layout Manager pour dire comment le Pane se comporte dans la JFrame.
        JPanel contentPane = new JPanel(new GridLayout(jeu.getSize(), jeu.getSize()));
        // Défini le placement des fils en gridLayout.

        for (int i = 0; i < jeu.getSize(); i++) {
            for (int j = 0; j < jeu.getSize(); j++) {
                // On créer un objet Bordure en utilisant l'interface Border, ici on utilisera la méthode createLineBorder pour avoir un LineBorder. 
                // Factory : Design patern qui permet de créer un nouvelle objet à partir d'une interface (ici BorderFactory)
                Border border = BorderFactory.createLineBorder(Color.darkGray, 5);
                // Dans chaque case du tableau on stock on composant JLabel.
                tabC[i][j] = new JLabel();
                // On défini pour ce composant une bordure avec setBorder.
                tabC[i][j].setBorder(border);
                // On définie l'alignement du composant pour qu'il soit au centre. 
                tabC[i][j].setHorizontalAlignment(SwingConstants.CENTER);


                contentPane.add(tabC[i][j]);

            }
        }
        // On remplace le ContentPane par notre Content pane que l'on vient de créer.
        setContentPane(contentPane); 
        ajouterEcouteurClavier();
        // Récupère les informations dans le tableau Jeu et met dans les labels du texte.
        rafraichir();

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

                            tabC[i][j].setText("");

                        } else {
                            tabC[i][j].setText(c.getValeur() + ""); // On lui met une valeur 2 , 4 .... 
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
        for(int i = 0; i < 4; i++) {
            jeu.setTabCases(1,i,2);
        }
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