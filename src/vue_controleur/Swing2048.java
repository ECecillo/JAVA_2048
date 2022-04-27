package vue_controleur;


import modele.Case;
import modele.Direction;
import modele.Jeu;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;

import static vue_controleur.constants.Constants.*;

import java.util.Observable;
import java.util.Observer;


/* Là où on va mettre en place le fonctionnement de la Fenêtre. */
public class Swing2048 extends JFrame implements Observer {
    private static final int PIXEL_PER_SQUARE = 60;
    // tableau de cases : i, j -> case graphique
    private JLabel[][] tabC;
    private Jeu jeu;
    /**
     * JLabel qui va afficher le score actuel dans le Jeu
     */
    private JLabel score;
    /**
     * JLabel qui va afficher le meilleur score qui se trouve dans le fichier .bestScore.
     */
    private JLabel bestScore;


    public Swing2048(Jeu _jeu) {
        jeu = _jeu;
        tabC = new JLabel[jeu.getSize()][jeu.getSize()];
        // Fonction qui s'occupe de terminer la fenêtre lorsque l'on appuie sur un bouton fermer.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Définie la taille de la JFrame en fonction de la taille de la fenêtre ().
        setSize(500, 625);
        this.setBackground(BACKGROUND_COLOR);
        this.setLocationRelativeTo(null);
        this.setFocusable(true);
        this.requestFocus();

        // JLabel : Composant permettant d'afficher du texte ou une image.
        // On alloue dans la mémoire un tableau 2D de Type JLabel.

        // Un Pane est associé à un JFrame, ce dernier est soit un contentPane soit un menuPane.
        // Un pane sera a son tour affecté à un Layout Manager pour dire comment le Pane se comporte dans la JFrame.
        JPanel content = (JPanel) this.getContentPane();
        setPanelBackground(content, Color.BLACK);

        JPanel grille = createGrilleJeu();
        grille.setPreferredSize(new Dimension(375,0));

        JPanel panneauHorizontal = createHorizontalPannel();
        panneauHorizontal.setPreferredSize(new Dimension(0, 75));

        // On ajoute les pannels dans notre container content.
        content.add(grille, BorderLayout.CENTER);
        content.add(panneauHorizontal, BorderLayout.NORTH);

        // On remplace le ContentPane par notre Content pane que l'on vient de créer.
        setContentPane(content);
        ajouterEcouteurClavier();
        // Récupère les informations dans le tableau Jeu et met dans les labels du texte.
        rafraichir();
    }

    // TODO : Ajouter un panel quand le jeu est terminée.
    
    /**
     * Fonction qui retourne un JPanel qui représente la grille du jeu 2048.
     * @return JPanel remplis avec des cases colorées.
     */
    private JPanel createGrilleJeu() {
        JPanel contentPane = new JPanel(new GridLayout(jeu.getSize(), jeu.getSize()));
        setPanelBackground(contentPane, BACKGROUND_COLOR);
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
                setFont(tabC[i][j], "Bold", 64);
                setTextColor(tabC[i][j], COLOR_VALUE_LIGHT);
                contentPane.add(tabC[i][j]);
            }
        }
        return contentPane;
    }

    /**
     * Créer le composant JPanel qui nous permettra d'afficher le titre, le score, le meilleur score et rejouer.
     * @return
     */
    private JPanel createHorizontalPannel() {
        JPanel sidePan = new JPanel(new GridLayout(1, 3));
        sidePan.add(createText("2048", SwingConstants.CENTER, COLOR_VALUE_LIGHT, "Bold", 56));
        setPanelBackground(sidePan, WINDOW_BACKGROUND);
        /*
        JLabel title = createText("2048", SwingConstants.LEFT, COLOR_VALUE_LIGHT,"Bold", 54);
        JLabel score = createText("0", SwingConstants.CENTER, COLOR_VALUE_LIGHT,"Bold", 24);
        JLabel meilleurScore = createText("0", SwingConstants.CENTER, COLOR_VALUE_LIGHT,"Bold", 24);

        JButton rejouer = new JButton("Rejouer");

        sidePan.add(title);
        sidePan.add(score);
        sidePan.add(meilleurScore);
        sidePan.add(rejouer);*/
        sidePan.add(createScorePane());
        sidePan.add(createBestScorePane());
        sidePan.add(createReplayPane());

        return sidePan;
    }

    private JPanel createScorePane () {
        JPanel scorePane = new JPanel(new GridLayout(2,1));
        scorePane.setBackground(WINDOW_BACKGROUND);
        JLabel title = createText("SCORE", SwingConstants.CENTER, COLOR_VALUE_LIGHT,"Bold", 16);
        score = createText("", SwingConstants.CENTER, COLOR_VALUE_LIGHT,"Bold", 28);
        score.setHorizontalAlignment(SwingConstants.CENTER);
        scorePane.add(title);
        scorePane.add(score);
        return scorePane;
    }

    private JPanel createBestScorePane() {
        JPanel bestScorePane = new JPanel(new GridLayout(2,1));
        String score = Integer.toString(jeu.getBestScore());
        bestScorePane.setBackground(WINDOW_BACKGROUND);
        JLabel title = createText("Best", SwingConstants.CENTER, COLOR_VALUE_LIGHT,"Bold", 16);
        bestScore = createText(score, SwingConstants.CENTER, COLOR_VALUE_LIGHT,"Bold", 28);

        bestScorePane.add(title);
        bestScorePane.add(bestScore);
        return bestScorePane;
    }

    private JPanel createReplayPane() {
        JPanel buttonPane = new JPanel();
        buttonPane.setBackground(WINDOW_BACKGROUND);
        JButton resetButton = new JButton();
        // Charge l'image que l'on a dans le dossier assets.
        String rest_path = "../assets/reset.png";
        resetButton.setIcon(new ImageIcon( getClass().getResource(rest_path)));
        resetButton.setPreferredSize(new Dimension(50, 50));
        resetButton.setHorizontalAlignment(SwingConstants.CENTER);
        resetButton.setVerticalAlignment(SwingConstants.BOTTOM);
        resetButton.setBorderPainted(false);
        resetButton.setContentAreaFilled(false);
        resetButton.setFocusPainted(false);
        resetButton.setOpaque(false);
        resetButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                jeu.reset();
                rafraichir();
                ajouterEcouteurClavier();
                setFocusable(true);
                requestFocus();
            }
        });
        buttonPane.add(resetButton);
        return buttonPane;
    }

    private JPanel createEndGamePane() {
        JPanel endGamePane = new JPanel();
        endGamePane.setBackground(TRANSPARENT_WINDOW_BACKGROUND);
        JLabel text = createText("Jeu terminé !", SwingConstants.CENTER, COLOR_VALUE_LIGHT, "Bold", 32);
        //JPanel replay = createReplayPane();
        text.setSize(new Dimension(0, 200));

        JButton replay = new JButton("recommencer");
        replay.setFont( new Font("Arial Bold", Font.PLAIN, 24));
        replay.setPreferredSize(new Dimension(200, 50));
        replay.setForeground(COLOR_VALUE_LIGHT);
        replay.setBackground(BACKGROUND_COLOR);

        endGamePane.add(text, BorderLayout.NORTH);
        endGamePane.add(replay, BorderLayout.CENTER);
        return endGamePane;
    }

    private JLabel createText(String text, int alignement,Color color, String style, int size) {
        JLabel textResult = new JLabel(text);
        if(!(alignement == -1)) {
            textResult.setHorizontalAlignment(alignement);
        }
        setTextColor(textResult, color);
        setFont(textResult, style, size);

        return textResult;
    }

    private void setFont(JLabel label, String style,int size) {
        label.setFont(new Font("Arial " + style, Font.PLAIN, size));
    }

    private void setTextColor(JLabel label, Color color) {
        label.setForeground(color);
    }

    private void setPanelBackground(JPanel label, Color color) {
        label.setBackground(color);
        label.setOpaque(true);
    }

    /**
     * Fonction qui s'occupe de changer la couleur de fond du JLabel passé en paramètre avec la couleur.
     *
     * @param label Label dont on veut changer la couleur.
     * @param color Couleur que l'on veut appliquer sur ce dernier.
     */
    private void setTextBackgroundColor(JLabel label, Color color) {
        label.setBackground(color);
        label.setOpaque(true);
    }

    /**
     * Fonction qui selon la valeur d'une case va mettre une couleur de fond pour le JLabel passé en paramètre.
     *
     * @param label JLabel dont on veut set la couleur de fond.
     * @param value La valeur de la case que l'on regarde.
     */
    private void changeTextBackground(JLabel label, int value) {
        switch (value) {
            case 0 -> setTextBackgroundColor(label, BACKGROUND_COLOR);
            case 2 -> setTextBackgroundColor(label, COLOR_2);
            case 4 -> setTextBackgroundColor(label, COLOR_4);
            case 8 -> setTextBackgroundColor(label, COLOR_8);
            case 16 -> setTextBackgroundColor(label, COLOR_16);
            case 32 -> setTextBackgroundColor(label, COLOR_32);
            case 64 -> setTextBackgroundColor(label, COLOR_64);
            case 128 -> setTextBackgroundColor(label, COLOR_128);
            case 256 -> setTextBackgroundColor(label, COLOR_256);
            case 512 -> setTextBackgroundColor(label, COLOR_512);
            case 1024 -> setTextBackgroundColor(label, COLOR_1024);
            case 2048 -> setTextBackgroundColor(label, COLOR_2048);
        }
        if (value > 2048)
            setTextBackgroundColor(label, COLOR_OTHER);
    }

    /**
     * Correspond à la fonctionnalité de Vue : affiche les données du modèle
     */
    private void rafraichir() {

        // On passe en paramètre de InvokeLater une classe anonyme.
        SwingUtilities.invokeLater(new Runnable() { // demande au processus graphique de réaliser le traitement
            @Override
            public void run() {
                score.setText(Integer.toString(jeu.getScore()));
                if(jeu.jeu_terminee()) {
                    setContentPane( createEndGamePane());
                }
                for (int i = 0; i < jeu.getSize(); i++) {
                    for (int j = 0; j < jeu.getSize(); j++) {
                        Case c = jeu.getCase(i, j);
                        // Si la case du tableau est null on affiche une case avec un texte vide.
                        if (c == null || c.getValeur() == 0) {
                            tabC[i][j].setText("");
                            changeTextBackground(tabC[i][j], 0);
                        } else {
                            tabC[i][j].setText(c.getValeur() + ""); // On lui met une valeur 2 , 4 ....
                            changeTextBackground(tabC[i][j], c.getValeur());
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

        addKeyListener(new KeyAdapter() { // new KeyAdapter() { ... } est une instance de classe anonyme, il s'agit d'un objet qui correspond au controleur dans MVC
            @Override
            public void keyPressed(KeyEvent e) {
                System.out.println("Hello there");

                switch (e.getKeyCode()) {  // on regarde quelle touche a été pressée
                    case KeyEvent.VK_LEFT -> jeu.monTest(Direction.gauche);
                    // A changer, car ici à chaque case que l'on appuie on génère une nouvelle classe.
                    case KeyEvent.VK_RIGHT -> jeu.monTest(Direction.droite);
                    case KeyEvent.VK_DOWN -> jeu.monTest(Direction.bas);
                    case KeyEvent.VK_UP -> jeu.monTest(Direction.haut);
                }
            }
        });
    }

    // Fonction exécutée lorsque Jeu envoie une notification à la vue avec setChanged et NotifyObserver.
    @Override
    public void update(Observable o, Object arg) {
        rafraichir();
    }
}