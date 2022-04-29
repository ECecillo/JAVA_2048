package modele;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.awt.Point;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Jeu extends Observable implements Executor {

    /**
     * Tableau que l'on affiche avec la Console et Swing et sur lequel on effectue des opérations.
     */
    private Case[][] tabCases;
    /**
     * Hahsmap qui nous permet de retrouver les coordonnées d'une case identifiée par son instance.
     */
    private HashMap<Case, Point> IndexCase;
    /**
     * Regarde combien de case dispo il reste dans notre tableau.
     */
    private int case_dispo;
    // TODO : Setup le score.
    /**
     * Meilleur score effectué sur le Jeu.
     */
    private int bestScore;
    /**
     * Score que le joueur est en train de faire.
     */
    private int score;

    public Jeu(int size) {
        tabCases = new Case[size][size];
        IndexCase = new HashMap<Case, Point>(size * size);
        case_dispo = size * size;

        String content = getFileContent("./src/.bestScore");
        assert content != null;
        bestScore = Integer.parseInt(content);
        initialise_zero();
    }

    /**
     * Procédure qui s'occupe d'enlever toutes les références de case dane le tableau TabCase et remet à 0 ce dernier.
     */
    public void reset() {
        IndexCase.clear();
        // Peut être inutile car le garbage collector s'occupera des cases que l'on utilise plus mais bon.
        for (int i = 0; i < getSize(); i++) {
            for (int j = 0; j < getSize(); j++) {
                tabCases[i][j] = null;
            }
        }
        initialise_zero();
        score = 0;
    }

    /**
     * Fonction de Controle qui va s'occuper d'appeler toutes les cases pour qu'ellle s'échange ou fusionne leur valeur.
     *
     * @param direction Direction vers laquelle les cases vont devoir s'interchanger ou fusionner.
     */
    public void action(Direction direction) {
        // TODO : Essayer de mettre ça autre part pour pas nous bruler les yeux.
        int direction_colonne = 0, // Indiquera si on doit regarder la case à côté de notre case [i][j]
                direction_ligne = 0, // Indiquera si on doit regarder la case en dessous de notre [i][j].
                colonne_start = 0, // Indiquera à partir de quelle colonne on commence notre boucle.
                ligne_start = 0, // ... à partir de quelle ligne on commence.
                colonne_end = this.getSize(), // Jusqu'où on va s'arrêter.
                ligne_end = this.getSize(); // On a également besoin de savoir jusqu'où on va.
        switch (direction) {
            case haut -> {
                ligne_start = (this.getSize() * -1) + 1; // On part de la ligne 3.
                direction_ligne = -1; // On décrémente.
                ligne_end = 0; // jusqu'à 0.
            }
            // On part de la colonne et ligne 0 et on incrémente jusqu'à la taille de la grille.
            case droite -> direction_colonne = 1;
            case bas -> direction_ligne = 1;
            case gauche -> {
                colonne_start = (this.getSize() * -1) + 1; // On part de la dernière colonne.
                direction_colonne = -1; // On décrémente la colonne de -1.
                colonne_end = 0; // Jusqu'à 0.
            }
            default -> throw new IllegalStateException("Cette direction n'existe pas.");
        }
        // TODO : Nettoyage !
        //System.out.format("Nous allons nous déplacer vers %s \nDonc i est égale à %s\nEt j est égale à %s\n",
        //direction, direction_ligne, direction_colonne);
        //System.out.println("On commence à la ligne d'indice " + Math.abs(ligne_start) + " et de colonne " + Math.abs(colonne_start));
        //System.out.println("On fini à la ligne " + Math.abs(ligne_end) + " et colonne " + Math.abs(colonne_end));

        // TODO : Essayer de mettre en place un Thread Pool qui lance pour chaque i,j un thread qui s'occupe de changer son pote.
        // TODO : Implémenter la logique suivante :

        //System.out.println(IndexCase);
        // Pour chacune des cases on va appeler leur fonction déplace qui va s'occuper de gérer leur changement de valeur.

        for (int i = ligne_start; i < ligne_end; i++) {
            for (int j = colonne_start; j < colonne_end; j++) {
                // Pour les directions haut et gauche, ligne_start et colonne sont négatifs.
                // Afin d'éviter un problème d'indice on les reconverties en nombre positif.
                int absolute_i = Math.abs(i);
                int absolute_j = Math.abs(j);

                int index_i_voisin = absolute_i + direction_ligne;
                int index_j_voisin = absolute_j + direction_colonne;

                // Si la case voisine est bien dans la grille, on peut faire l'échange avec la case (i,j).
                if (!((index_i_voisin > (tabCases.length - 1) || index_i_voisin < 0)
                        || (index_j_voisin > (tabCases.length - 1) || index_j_voisin < 0))) {
                    // On demande à la case [i][j] de se déplacer avec sa case voisine aux coordonnées [index_i_voisin][index_j_voisin].
                    //System.out.println("La valeur de i est : " + absolute_i + " et la valeur j est : " + absolute_j);
                    //System.out.println("La valeur i du voisin : " + index_i_voisin + " et son j est : " + index_j_voisin);

                    Point voisin = IndexCase.get(tabCases[index_i_voisin][index_j_voisin]);

                    // On récupère le pas que l'on va appliquer pour savoir si on doit consulter la case dans 2 index plus
                    // loin pour éviter de refusionner ou continuer avec la case suivante.
                    int result = 0;
                    tabCases[absolute_i][absolute_j].deplacer(voisin, this, result);
                    // Si la pas est 1, on doit vérifier la valeur du voisin d'avant (d'habitude on regarde celui d'après)
                    // et voir si cette dernière est différente de 0, si c'est le cas on fusionne avec notre case [i][j]
                    // qui juste avant a été fusionné et est donc null.
                    int voisin_avant_i = Math.abs(i - 1);
                    int voisin_avant_j = Math.abs(j - 1);
                    if(result == 1
                            && ((voisin_avant_i > 0 && voisin_avant_i < getSize() - 1)
                            && (voisin_avant_j > 0 && voisin_avant_j < getSize() - 1))) {
                        // On appelle le deplacement sur le vosin d'avant et pas la case i j.
                        Point case_actuel = IndexCase.get(tabCases[absolute_i][absolute_j]);
                        tabCases[voisin_avant_i][voisin_avant_j].deplacer(case_actuel, this, 0);
                        // Selon la touche directionnel, on doit augmenter l'indice qui va dans la direction que l'on
                        // regarde.
                        switch (direction) {
                            case haut, bas -> i += 1;
                            case gauche, droite -> j +=1;
                        }
                    }
                } else {
                    System.out.format("Skipping ligne %s et colonne %s \n", i, j);
                }
            }
        }
        // On a fini de déplacer toutes les Cases entre elles on va pouvoir ajouter 2 nouvelles valeurs dans le jeu.
        // Flemme de faire des copier-coller en plus on peut être amené à en générer plus donc on aura juste à changer le 2.
        // Lance dans un thread le calcul du score.
        calculLeScore();
        ajoute_nombre_aleatoire(1);
    }
    /**
     * Procédure qui lance dans un Thread le calcul du score et va changer notre score par le résultat.
     */
    private void calculLeScore() {
        new Thread() {
            @Override
            public void run() {
                int score_tour = 0;
                for (Case[] tabCase : tabCases) {
                    for (int j = 0; j < tabCases.length; j++) {
                        score_tour += tabCase[j].getValeur();
                        System.out.println("Case " + tabCase[j].getValeur());
                    }
                }
                score = score_tour;
            }
        }.start();
    }
    /**
     * Fonction qui lance dans un thread en parallèle de l'éxécution principale des fonctions comme action ...
     * @param direction La direction que l'on passe à la fonction action.
     */
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

    public synchronized void ajoute_nombre_aleatoire(int _nbraleat) {
        int compteur = _nbraleat;
        /*
        if(case_dispo == 1)
        {
            compteur = 1;
        }
        else if(case_dispo > 1)
        {
            compteur = 2;
        }

        */
        // Boucle qui ajoute les nombres selon un compteur de cases.
        while(compteur > 0) {
            if (case_dispo <= 0) {
                System.out.println("Le Jeu est plein on ne peut plus continuer.");
                // Pour le moment on arrête le jeu, on devra changer par une fonction qui s'occupe de gérer la fin du jeu.
            }
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
        case_dispo = tabCases.length*tabCases.length;
        ajoute_nombre_aleatoire(1);
    }

    public boolean jeu_terminee() {
        if(case_dispo <= 0) {
            System.out.println("Le Jeu est terminé on affiche un overlay.");
            return true;
        }
        return false;
    }

    //  ================= Méthodes Static  =======================

    /**
     * Fonction qui s'occupe de récupérer le contenu d'un fichier et de renvoyer le scanner qui est le contenu de ce dernier.
     * @param file_path Le chemin vers le fichier dont on veut récupérer les éléments à partir de la racine du projet.
     * @return Scanner dont on peut extraire les données.
     */
    public static String getFileContent(String file_path) {
        try {
            File fichier = new File(file_path);
            Scanner reader = new Scanner(fichier);
            String content = reader.nextLine();
            reader.close();
            return content;
        } catch (FileNotFoundException e) {
            System.out.println("No data found");
            //e.printStackTrace();
            return null;
        }
    }

    /**
     * Ecris dans un fichier le contenu passé en paramètre.
     * @param content String que l'on veut écrire dans le fichier
     * @param file_path Chemin du fichier que l'on veut créer.
     */
    public static void createFileWithContent(String file_path, String content) {
        try {
            FileWriter writer = new FileWriter(file_path);
            writer.write(content);
            writer.close();
            System.out.println("Ecriture réussite");
        } catch (IOException e) {
            System.out.println("Écriture du meilleur score impossible...");
            e.printStackTrace();
        }
    }
    /**
     * Fonction qui vérifie si une valeur est bien dans l'intervalle passé en paramètre.
     * @param value valeur que l'on veut vérifier.
     * @param upperBound Le max de l'intervalle.
     * @param lowerBound Le min de l'intervalle.
     * @return true si la valeur est dans l'intervalle false sinon.
     */
    public static boolean checkInBound(int value, int upperBound, int lowerBound) {
        return value < upperBound && value > lowerBound;
    }

    /**
     * Procédure qui affiche les coordonnées d'un point passé en paramètre.
     *
     * @param p Point dont on veut voir les coordonnées.
     */
    public static void afficheCoordonnees(Point p) {
        System.out.format("\nX : %s \nY : %s \n", p.x, p.y);
    }


    // =============== Accesseur et Mutateur ============================= Bon courage

    /**
     * @return Récupère la taille du tableau.
     */
    public int getSize() {
        return tabCases.length;
    }

    /**
     * @param i entier qui correspond à la ligne dans le tableau de jeu.
     * @param j entier qui correspond à la colonne.
     * @return La Case qui sont à la ligne i et la colonne j.
     */
    public Case getCase(int i, int j) {
        return tabCases[i][j];
    }

    /**
     * @param p Point qui correspond aux coordonnées d'une case.
     * @return La Case qui correspond à ces coordonnées.
     */
    public Case getCase(Point p) {
        return tabCases[p.x][p.y];
    }

    /**
     * @param i               La ligne où se trouve la case que l'on veut changer.
     * @param j               La colonne où se trouve la case que l'on veut changer.
     * @param nouvelle_valeur La nouvelle valeur de cette case.
     */
    public void setTabCases(int i, int j, int nouvelle_valeur) {
        tabCases[i][j].setValeur(nouvelle_valeur);
    }

    /**
     * @param p               Coordonnées d'une case où x et y sont les i et j.
     * @param nouvelle_valeur Nouvelle valeur de la Case qui se trouve dans le tableau de Case.
     */
    public void setTabCases(Point p, int nouvelle_valeur) {
        tabCases[p.x][p.y].setValeur(nouvelle_valeur);
    }

    /**
     * @param p             Coordonnées de la case que l'on veut changer.
     * @param nouvelle_case La Case qui va remplacer celle qui se trouve au point p dans le tableau de Case.
     */
    public void setTabCases(Point p, Case nouvelle_case) {
        tabCases[p.x][p.y] = nouvelle_case;
    }

    /**
     * @param i             La ligne où se trouve la case que l'on veut changer.
     * @param j             La colonne où se trouve la case que l'on veut changer.
     * @param nouvelle_case La Case qui va remplace celle qui se trouve au point p dans le tableau de Case.
     */
    public void setTabCases(int i, int j, Case nouvelle_case) {
        tabCases[i][j] = nouvelle_case;
    }

    public void setTabCases(Case ancienne_case, Case nouvelle_case) {
        Point p = IndexCase.get(ancienne_case);
        setTabCases(p, nouvelle_case);
    }

    /**
     * Mutateur qui s'occupe de supprimer la case que l'on veut changer dans la hashmap et la remet avec une nouvelle valeur.
     *
     * @param c               Case que l'on veut changer.
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
     *
     * @param c             Ancienne case que l'on veut remplacer.
     * @param nouvelle_case La nouvelle case avec une valeur différente.
     */
    public void setIndexCase(Case c, Case nouvelle_case) {
        // Les coordonnées de la case reste la même on change seulement sa valeur.
        Point coordo_case = IndexCase.get(c);
        IndexCase.remove(c);
        IndexCase.put(nouvelle_case, coordo_case);
    }

    public void setCase_dispo() {
        case_dispo++;
    }

    /**
     * Récupère les coordonées de la case dans la HashMap.
     * @param key La case que l'on veut récupérer.
     * @return un Point qui correspond à la case que l'on a passé en paramètre.
     */
    public Point getCaseFromHash(Case key) {
        return IndexCase.get(key);
    }

    public int getBestScore () {
        return bestScore;
    }
    public int getScore () {
        return score;
    }
    // La fonction d'execution du pool de processus qui est appelée par l'Executor
    @Override
    public void execute(Runnable command) {
        command.run();
    }

}
