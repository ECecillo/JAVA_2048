package modele;

import java.util.*;
import java.awt.Point;

public class Jeu extends Observable {

    // C'est le tableau que l'on utilise pour appliquer des opérations sur le Jeu.
    private Case[][] tabCases;
    // Créer une Hash map pour retrouver l'indice de la classe.
    private HashMap<Case, Point> IndexCase;

    // redéfinir le Equals.

    // Créer un nombre aléatoire en utilisant une seed à 4 qui nous permettra
    // d'utiliser le nextInt.
    private static Random rnd = new Random(4);

    public Jeu(int size) {
        tabCases = new Case[size][size];
        IndexCase = new HashMap<Case, Point>(size * size);
        initialise_zero();
        monTest();
    }

    public void monTest() {
        new Thread() {
            public void run() {
                //System.out.println("Hello there");
                ajoute_nombre_aleatoire();
                ajoute_nombre_aleatoire();
            }
        }.start();
        // Notification de la vue, suite à la mise à jour du champ lastValue.
        setChanged();
        // Va appeler la méthode update dans le Swing2048 (Vue) pour mettre à jour
        // l'affichage Graphique.
        notifyObservers();
    }

    public int getSize() {
        return tabCases.length;
    }

    public Case getCase(int i, int j) {
        return tabCases[i][j];
    }

    // Récupère les coordonées de la case dans la HashMap.
    public Point getCaseFromHash(Case key) {
        return IndexCase.get(key);
    }

    // Nous permettra de savoir ce qui se passe dans le tableau de Jeu.
    public void Debug_Jeu() {
        // On récupère chaque clés de la HashMap et on les stocks dans un Set.
        Set<Case> Hash_index = IndexCase.keySet();
        // On va parcourir le Set en récupérant les coordonnées avec les clés du Set.
        for (Case index : Hash_index) {
            // On stock la valeur à cette index dans un Point.
            Point coordo_case = IndexCase.get(index);
            // On affiche les coordonnées de la case.
            String coord_x = "X : " + coordo_case.y + "\n";
            String coord_y = "Y : " + coordo_case.x + "\n";

            System.out.println(coord_x + coord_y);
        }
    }

    public void Debug_Jeu(Case c, Point debug_point) {
        // Comme on appelle cette méthode dans un thread on sécurise l'affichage pour ne
        // pas tout avoir en même temps.
        synchronized (this) {
            // Permettra d'affciher la case et le point que l'on met dans la HashTable.
            String coord_x = "X : " + debug_point.y + "\n";
            String coord_y = "Y : " + debug_point.x + "\n";
            String valeur_case = "La valeur de la case est : " + c.getValeur() + "\n";

            System.out.println("Case aux coordonnées\n" + coord_x + coord_y + valeur_case);
        }
    }

    // Méthode naïve pour récupérer les cases qui sont null.
    // Méthode utilitaire qui permet pour n'importe quelle type de Map (Hashmap,
    // TreeMap ect ...) d'avoir toutes les clés qui ont une certains valeur V.
    private List<Case> getAllCaseFromValue(int value) {
        List<Case> listOfKeys = null;

        listOfKeys = new ArrayList<>();
        // A optimiser.
        for (int i = 0; i < tabCases.length; i++) {
            for (int j = 0; j < tabCases.length; j++) {
                if (tabCases[i][j].getValeur() == value) {
                    listOfKeys.add(tabCases[i][j]);
                }
            }
        }

        // Return the list of keys whose value matches with given value.
        return listOfKeys;
    }

    private synchronized void ajoute_nombre_aleatoire() {
        // Opti Possible : Après avoir effectué les déplacements et les fusions on
        // devrait avoir des valeurs null si on reparcours dans le même sens.
        // On récupère toutes les clés (cases) qui ont une valeur null.
        List<Case> liste_case_null = getAllCaseFromValue(0);
        // On stock la taille de la Liste pour ensuite choisir une clé aléatoire dans la
        // liste.
        int taille_liste = liste_case_null.size();

        // Génère un index aléatoire.
        Random rnd = new Random(taille_liste);

        // On choisi une Case aléatoire dans la Liste qui a une valeur Null.
        Case case_aleatoire = liste_case_null.get(rnd.nextInt(taille_liste));
        // On récupère les coordonnées de la case à partir de la hashmap pour
        // pouvoiraussi changer la valeur de la case du tableau.
        Point coordo_case = IndexCase.get(case_aleatoire);
        // On doit créer une autre valeur aléatoire pour savoir si on met un 2 ou un 4.
        Random rnd_value = new Random();
        // On change la valeur de la case par une valeur aléatoire.
        float random_value = rnd_value.nextFloat();
        // On créer une variable qui va stocker notre nouvelle case.
        Case nouvelle_case = null;
        // Génère la nouvelle case en fonction de la valeur aléatoire.
        if (random_value > 0.5) {
            nouvelle_case = new Case(2, case_aleatoire.getID());
        } else {
            nouvelle_case = new Case(4, case_aleatoire.getID());
        }
        // On remplace l'ancienne case dans notre tableau par la nouvelle.
        tabCases[coordo_case.x][coordo_case.y] = nouvelle_case;
        // On oublie pas de changer la case dans la HashMap.
        // On a décidé de faire une suppression puis de remettre la Case dans la
        // HashMap.
        IndexCase.remove(case_aleatoire);
        IndexCase.put(nouvelle_case, coordo_case);
        // Autre version possible apparemment plus optimisé : map.put( nouvelle_case, map.remove(case_aleatoire) );
        System.out.println(coordo_case);

    }

    private void test_equals() throws Exception {
        // Test la fonction d'égalité qui doit remplir les 3 règles du contrat
        // (Réflexivité, Symétrie, Transitivité).
        Case.should_be_equals();
    }

    // Initialise notre tableau à Null.
    public void initialise_zero() {
        int id = 0;
        for (int i = 0; i < tabCases.length; i++) {
            for (int j = 0; j < tabCases.length; j++) {
                Case case_local = new Case(0, id);
                Point coordonne_case = new Point(i, j);
                tabCases[i][j] = case_local;
                IndexCase.put(case_local, coordonne_case);
                id++;
            }
        }
    }

    public void rnd() { // Processus métier.
        new Thread() { // permet de libérer le processus graphique ou de la console
            public void run() { // On définit ce que le thread va faire.
                int r;
                // On stock la case que l'on est en train de regarder.
                Case case_actuel;
                int id_case = 0;

                for (int i = 0; i < tabCases.length; i++) {
                    for (int j = 0; j < tabCases.length; j++) {
                        // On créer un nombre aléatoire entre 0 et 2.
                        r = rnd.nextInt(3);
                        // On stock les coordonnées de la case dans un objet Point.
                        Point coordonnees_case = new Point(i, j);
                        // génère un identifiant unique pour la case.
                        id_case++;
                        tabCases[i][j] = new Case(0, id_case);
                        case_actuel = tabCases[i][j];

                        switch (r) { // Selon la valeur r on met soit null, 2 ou 4 dans la case i,j du tableau.
                            case 0:
                                tabCases[i][j] = null;
                                case_actuel = tabCases[i][j];
                                IndexCase.put(case_actuel, coordonnees_case);
                                // Debug_Jeu(case_actuel, coordonnees_case);
                                break;
                            case 1:
                                tabCases[i][j] = new Case(2, id_case);
                                case_actuel = tabCases[i][j];
                                IndexCase.put(case_actuel, coordonnees_case);
                                // Affiche la valeur que l'on a mis dans la Hash Table.
                                // Debug_Jeu(case_actuel, coordonnees_case);
                                break;
                            case 2:
                                tabCases[i][j] = new Case(4, id_case);
                                case_actuel = tabCases[i][j];
                                IndexCase.put(case_actuel, coordonnees_case);
                                // Affiche la valeur que l'on a mis dans la Hash Table.
                                // Debug_Jeu(case_actuel, coordonnees_case);
                                break;
                        }
                    }
                }
                try {
                    // tabCases[0][0].equals(tabCases[1][0]);
                    test_equals();
                    System.out.println("Tests passed Successfully !");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }.start();

        // Notification de la vue, suite à la mise à jour du champ lastValue.
        setChanged();
        // Va appeler la méthode update dans le Swing2048 (Vue) pour mettre à jour
        // l'affichage Graphique.
        notifyObservers();

    }

}
