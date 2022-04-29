import modele.Jeu;
import vue_controleur.Console2048;
import vue_controleur.Swing2048;

import java.util.Arrays;
import java.util.function.Function;

public class Main {

    public static void main(String[] args) {
        //mainConsole();
        mainSwing();

    }

    public static void mainConsole() {
        Jeu jeu = new Jeu(4);
        Console2048 vue = new Console2048(jeu);
        jeu.addObserver(vue);

        vue.start();

    }

    public static void mainSwing() {

        Jeu jeu = new Jeu(4);
        Jeu jeu2 = new Jeu(4);

        // On créer 2 JFrame qui vont contenir chacune une instance différente
        // de 2048 mais auxquels on affecte les mêmes listener.
        Swing2048 vue = new Swing2048(jeu, jeu2);

        //
        jeu.addObserver(vue);
        jeu2.addObserver(vue);
        // On affiche.
        vue.setVisible(true);
    }



}
