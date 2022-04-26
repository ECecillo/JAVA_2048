package modele;

//import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Jeu extends Observable implements Executor {

    // C'est le tableau que l'on utilise pour appliquer des opérations sur le Jeu.
    private Case[][] tabCases;
    // Créer une Hash map pour retrouver l'indice de la classe.
    private HashMap<Case, Point> IndexCase;

    /**
     * Regarde combien de case dispo il reste dans notre tableau.
     */
    private int case_dispo;

    // Créer un nombre aléatoire en utilisant une seed à 4 qui nous permettra
    // d'utiliser le nextInt.
    private static Random rnd = new Random(4);

    public Jeu(int size) {
        tabCases = new Case[size][size];
        IndexCase = new HashMap<Case, Point>(size * size);
        case_dispo = size * size;
        initialise_zero();
        //monTest();
    }

    // Récupère les coordonées de la case dans la HashMap.
    public Point getCaseFromHash(Case key) {
        return IndexCase.get(key);
    }


    /**
     * Fonction de Controle qui va s'occuper d'appeler toutes les cases pour qu'ellle s'échange ou fusionne leur valeur.
     * @param direction Direction vers laquelle les cases vont devoir s'interchanger ou fusionner.
     */
    public void action(Direction direction) {
        // Selon la direction demandé par l'utilisateur,
        int direction_colonne = 0, direction_ligne = 0, // Indique dans quelle sens on va incrémenter nos index.
                colonne_start = 0, ligne_start = 0, // Si on va vers le haut ou gauche on part des extrémités.
                colonne_end = this.getSize(), ligne_end = this.getSize(); // On a également besoin de savoir jusqu'où on va.
        switch (direction) {
            case haut -> {
                ligne_start = (this.getSize()*-1); // On part de la ligne 3.
                direction_ligne = -1; // On décrémente.
                ligne_end = 0; // jusqu'à 0.
            }
            // On part de la colonne et ligne 0 et on incrémente jusqu'à la taille de la grille.
            case droite -> direction_colonne = 1;
            case bas -> {
                direction_ligne = 1;
            }
            case gauche -> {
                colonne_start = (this.getSize()*-1); // On part de la dernière colonne.
                direction_colonne = -1; // On décrémente la colonne de -1.
                colonne_end = 0; // Jusqu'à 0.
            }
            default -> throw new IllegalStateException("Cette direction n'existe pas.");
        }
        System.out.format("Nous allons nous déplacer vers %s \nDonc i est égale à %s\nEt j est égale à %s\n",
                direction, direction_ligne, direction_colonne);
        System.out.println("On commence à la ligne d'indice " + Math.abs(ligne_start) + " et de colonne " + Math.abs(colonne_start));
        System.out.println("On fini à la ligne " + Math.abs(ligne_end) + " et colonne " + Math.abs(colonne_end));

        System.out.println(IndexCase);
        // Pour chacune des cases on va appeler leur fonction déplace qui va s'occuper de gérer leur changement de valeur.
        for (int i = ligne_start; i < ligne_end; i++) {
            for (int j = colonne_start; j < colonne_end; j++) {
                // Pour les directions haut et gauche, ligne_start et colonne sont négatifs.
                // Afin d'éviter un problème d'indice on les reconverties en nombre positif.
                int absolute_i = i < 0 ? Math.abs(i + 1) : i;
                int absolute_j = j < 0 ? Math.abs(j + 1) : j;

                int index_i_voisin = absolute_i + direction_ligne;
                int index_j_voisin = absolute_j + direction_colonne;
                // Si la case voisine est bien dans la grille, on peut faire l'échange avec la case (i,j).
                if(!((index_i_voisin > (tabCases.length - 1) || index_i_voisin < 0)
                        || (index_j_voisin > (tabCases.length - 1) || index_j_voisin < 0))) {
                    // On demande à la case [i][j] de se déplacer avec sa case voisine aux coordonnées [index_i_voisin][index_j_voisin].
                    System.out.println("La valeur est i est : " + absolute_i + " et la valeur j est : " + absolute_j);
                    System.out.println("La valeur i du voisin : " + index_i_voisin + " et son j est : " + index_j_voisin);

                    Point voisin = new Point(index_i_voisin, index_j_voisin);
                    //System.out.println(voisin);
                    //afficheCoordonnees(voisin);
                    tabCases[absolute_i][absolute_j].deplacer(voisin, this);
                }
                else {
                    System.out.format("Skipping ligne %s et colonne %s \n", i,j);
                }
            }
        }
        // On a fini de déplacer toutes les Cases entre elles on va pouvoir ajouter 2 nouvelles valeurs dans le jeu.
        // Flemme de faire des copier-coller en plus on peut être amené à en générer plus donc on aura juste à changer le 2.
        /*
        for (int i = 0; i < 2; i++) {
            ajoute_nombre_aleatoire();
        }
        */
        ajoute_nombre_aleatoire();
    }

    public static boolean checkInBound(int value, int upperBound, int lowerBound) {
        return value < upperBound && value > lowerBound;
    }

    /**
     * Procédure qui affiche les coordonnées d'un point passé en paramètre.
     * @param p Point dont on veut voir les coordonnées.
     */
    public static void afficheCoordonnees(Point p) {
        System.out.format("\nX : %s \nY : %s \n", p.x, p.y);
    }
    public void monTest(Direction direction) {
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(new Runnable() {
            @Override
            public void run() {
                try
                {
                    action(direction);
                } catch (Exception e) {
                    System.out.println("Le Jeu est plein on ne peut plus continuer.");
                }

                // Notification de la vue, suite à la mise à jour du champ lastValue.
                setChanged();
                // Va appeler la méthode update dans le Swing2048 (Vue) pour mettre à jour
                // l'affichage Graphique.
                notifyObservers();
            }
        });
    }
    /**
     * @param row : Ligne du tableau.
     * Tableau 1D de case où les valeurs égales à 0 ou null sont à droite.
     */
    private void slide (Case[] row) {
        int i = 0;
        System.out.println("Hello there " + IndexCase.get(row[0]));
        for (int j = row.length - 1; j > 0; j--) {
            //System.out.println("Hello there j : " + j + " i : " + i);
            /**
             * On check si la première valeur est null (i) et si la dernière est non null (j).
             * Si c'est le cas alors on inverse les deux et on réduit le j pour que à la prochaine itération on regarde celui d'avant.
             * Si les deux sont null on décrémente j et on reteste.
             * Si les deux ont des valeurs non null on décrémente juste j.
             * Si j <= i on arrête, car on a trié le tout.
             * Complexité O(n) -> pas ouf.
             */
            if(j <= i ) {
                break;
            }
            if((row[i].getValeur() == 0 || row[i] == null)
                    && (row[j].getValeur() != 0 || row[j] != null)) {
                Case swap = row[i]; // Case Null.
                row[i] = row[j]; // On met celle avec une Valeur dans la non null.
                row[j] = swap; // On met la Null dans l'ancienne case.
                Point coord_j = IndexCase.get(row[j]);
                Point coord_swap = IndexCase.get(swap);

                // On oublie pas de changer les cases dans la HashMap.
                // On a décidé de faire une suppression puis de remettre les Case dans la HashMap.
                IndexCase.remove(swap);// Coordonnées de l'ancien i.
                IndexCase.put(swap, coord_j); // Les nouvelles coordonnées de i sont celles de j.

                IndexCase.remove(row[j]);
                IndexCase.put(row[j], coord_swap); // Les nouvelles coordonnées de j sont celles de l'ancien i.

                i++;
            }
        }
    }

    /**
     *
     * @param array : Tableau / Object 2D dont on veut extraire la colonne.
     * @param index : La colonne que l'on veut extraire du tableau 2D.
     * @return Un Tableau qui contient les éléments de la colonne que l'on a passé en paramètre.
     */
    private static Object[] getColumn(Object[][] array, int index) {
        Object[] column = new Object[array[0].length];
        for (int i = 0; i < column.length; i++) {
            column[i] = array[i][index];
        }
        return column;
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

    // Fonction qui ajoute entre 1 et 2 nombres aléatoire selon le nombre de cases disponibles
    public synchronized void ajoute_nombre_aleatoire() {
        int compteur = 1;
        if(case_dispo == 1)
        {
            compteur = 1;
        }
        else if(case_dispo > 1)
        {
            compteur = 2;
        }
        // Boucle qui ajoute les nombres selon un compteur de cases.
        while(compteur > 0)
        {
            case_dispo--;
            // Opti Possible : Après avoir effectué les déplacements et les fusions on
            // devrait avoir des valeurs null si on reparcours dans le même sens.
            // On récupère toutes les clés (cases) qui ont une valeur null.

            List<Case> liste_case_null = getAllCaseFromValue(0);
            //System.out.println(liste_case_null);
            // On stock la taille de la Liste pour ensuite choisir une clé aléatoire dans la
            // liste.
            int taille_liste = liste_case_null.size();

            // Génère un index aléatoire.
            Random rnd = new Random(taille_liste);

            // On choisi une Case aléatoire dans la Liste qui a une valeur Null.
            Case case_aleatoire = liste_case_null.get(rnd.nextInt(taille_liste));
            // On récupère les coordonnées de la case à partir de la hashmap pour
            // pouvoir aussi changer la valeur de la case du tableau.
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
            setIndexCase(case_aleatoire, nouvelle_case);
            setTabCases(nouvelle_case, nouvelle_case);
            // On oublie pas de changer la case dans la HashMap.
            compteur--;
        }

    }

    /**
     * Fonction qui appel la méthode static dans Case pour comparer 2 case entre elle.
     * @param case1 Case source.
     * @param case2 Case que l'on va comparer.
     * @throws Exception
     */
    private void test_equals(Case case1, Case case2) throws Exception {
        // Test la fonction d'égalité qui doit remplir les 3 règles du contrat
        // (Réflexivité, Symétrie, Transitivité).
        Case.equal_Case(case1, case2);
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
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(new Runnable() {
            @Override
            public void run() {
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
                    //test_equals();
                    System.out.println("Tests passed Successfully !");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // Notification de la vue, suite à la mise à jour du champ lastValue.
        setChanged();
        // Va appeler la méthode update dans le Swing2048 (Vue) pour mettre à jour
        // l'affichage Graphique.
        notifyObservers();

    }


    // Accesseur et Mutateur.

    /**
     *
     * @return Récupère la taille du tableau.
     */
    public int getSize() {
        return tabCases.length;
    }

    /**
     *
     * @param i entier qui correspond à la ligne dans le tableau de jeu.
     * @param j entier qui correspond à la colonne.
     * @return La Case qui sont à la ligne i et la colonne j.
     */
    public Case getCase(int i, int j) {
        return tabCases[i][j];
    }

    /**
     *
     * @param p Point qui correspond aux coordonnées d'une case.
     * @return La Case qui correspond à ces coordonnées.
     */
    public Case getCase(Point p) {
        return tabCases[p.x][p.y];
    }

    /**
     *
     * @param i La ligne où se trouve la case que l'on veut changer.
     * @param j La colonne où se trouve la case que l'on veut changer.
     * @param nouvelle_valeur La nouvelle valeur de cette case.
     */
    public void setTabCases(int i, int j, int nouvelle_valeur) {
        tabCases[i][j].setValeur(nouvelle_valeur);
    }

    /**
     *
     * @param p Coordonnées d'une case où x et y sont les i et j.
     * @param nouvelle_valeur Nouvelle valeur de la Case qui se trouve dans le tableau de Case.
     */
    public void setTabCases(Point p, int nouvelle_valeur) {
        tabCases[p.x][p.y].setValeur(nouvelle_valeur);
    }

    /**
     *
     * @param p Coordonnées de la case que l'on veut changer.
     * @param nouvelle_case La Case qui va remplacer celle qui se trouve au point p dans le tableau de Case.
     */
    public void setTabCases(Point p, Case nouvelle_case) {tabCases[p.x][p.y] = nouvelle_case;}

    /**
     *
     * @param i La ligne où se trouve la case que l'on veut changer.
     * @param j La colonne où se trouve la case que l'on veut changer.
     * @param nouvelle_case La Case qui va remplace celle qui se trouve au point p dans le tableau de Case.
     */
    public void setTabCases(int i, int j, Case nouvelle_case) {tabCases[i][j] = nouvelle_case;}
    public void setTabCases(Case ancienne_case, Case nouvelle_case) {
        Point p = IndexCase.get(ancienne_case);
        setTabCases(p, nouvelle_case);
    }

    /**
     * Mutateur qui s'occupe de supprimer la case que l'on veut changer dans la hashmap et la remet avec une nouvelle valeur.
     * @param c Case que l'on veut changer.
     * @param nouvelle_valeur Entier qui représente la nouvelle valeur de la case.
     */
    public void setIndexCase(Case c, int nouvelle_valeur) {
        // On récupère les anciennes données de la case à savoir ces coordonnées et son identifiant.
        Point coordo_case = IndexCase.get(c);
        int id_case = c.getID();
        // On créer une nouvelle case avec la nouvelle valeur et son ancien id.
        Case nouvelle_case = new Case(nouvelle_valeur, id_case);
        // On a décidé de faire une suppression puis de remettre la Case avec ces nouvelles données dans la Hahsmap.
        IndexCase.remove(c);
        IndexCase.put(nouvelle_case, coordo_case);
        // Autre version possible apparemment plus optimisé : map.put( nouvelle_case, map.remove(case_aleatoire) );
    }

    /**
     * Mutateur qui remplace l'ancienne case par la nouvelle passé en paramètre.
     * @param c Ancienne case que l'on veut remplacer.
     * @param nouvelle_case La nouvelle case avec une valeur différente.
     */
    public void setIndexCase(Case c, Case nouvelle_case) {
        // Les coordonnées de la case reste la même on change seulement sa valeur.
        Point coordo_case = IndexCase.get(c);
        IndexCase.remove(c);
        IndexCase.put(nouvelle_case, coordo_case);
    }

    // La fonction d'execution du pool de processus qui est appelée par l'Executor
    @Override
    public void execute(Runnable command) {
        command.run();
    }
}
