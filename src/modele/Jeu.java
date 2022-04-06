package modele;

import java.util.Observable;
import java.util.Random;

public class Jeu extends Observable {

    // C'est le tableau que l'on utilise pour appliquer des opérations sur le Jeu.
    private Case[][] tabCases;
    // Créer une Hash map pour retrouver l'indice de la classe.

    // redéfinir le Equals.

    // Créer un nombre aléatoire en utilisant une seed à 4 qui nous permettra d'utiliser le nextInt.
    private static Random rnd = new Random(4);

    public Jeu(int size) {
        tabCases = new Case[size][size];
        rnd();
    }

    public int getSize() {
        return tabCases.length;
    }

    public Case getCase(int i, int j) {
        return tabCases[i][j];
    }


    public void rnd() { // Processus métier.
        new Thread() { // permet de libérer le processus graphique ou de la console
            public void run() { // On définit ce que le thread va faire.
                int r;

                for (int i = 0; i < tabCases.length; i++) {
                    for (int j = 0; j < tabCases.length; j++) {
                        // On créer un nombre aléatoire entre 0 et 2.
                        r = rnd.nextInt(3);

                        switch (r) { // 
                            case 0:
                                tabCases[i][j] = null;
                                break;
                            case 1:
                                tabCases[i][j] = new Case(2);
                                break;
                            case 2:
                                tabCases[i][j] = new Case(4);
                                break;
                        }
                    }
                }
            }

        }.start();

        // Notification de la vue, suite à la mise à jour du champ lastValue.
        setChanged();
        // Va appeler la méthode update dans le Swing2048 (Vue) pour mettre à jour l'affichage Graphique. 
        notifyObservers();


    }

}
