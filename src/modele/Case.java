package modele;

import java.awt.*;
import java.util.Objects;

public class Case {
    private int valeur;
    private int id; // L'id fera plus ou moins office d'identifiant unique pour une case, comme on
                    // ne peut pas utiliser leur position.

    // On compare 2 case en fonction de leur valeur, voir si on peut pas récup leur
    // position.
    @Override
    public boolean equals(Object obj) {
        // if both the object references are
        // referring to the same object.
        if (this == obj) {
            return true;
        }

        // it checks if the argument is of the
        // type Geek by comparing the classes
        // of the passed argument and this object.
        // if(!(obj instanceof Geek)) return false; ---> avoid.
        if (obj == null || obj.getClass() != this.getClass())
            return false;

        // type casting of the argument.
        Case aCase = (Case) obj;
        // comparing the state of argument with
        // the state of 'this' Object.
        return (this.valeur == aCase.valeur && this.id == aCase.id);
    }

    // Fonction qui permet de retourner un index pour la Hashmap.
    /*@Override
    public int hashCode() {
        // Règle 1 : 2 objets égaux doivent avoir le même Hashcode.
        // Règle 2 : 2 objets différent peuvent avoir le même hashCode, il faut alors
        // gérer la collision.
        int res = 1;
        int prime = 42;
        // On génère un code en fonction de l'id et de la valeur.
        res = (res * prime) + Objects.hash(id);
        res = (res * prime) + Objects.hash(valeur);
        // Note : Si on a plusieurs tableau on risque d'avoir un Hashcode égale il faut
        // donc gérer.
        return res;
    }*/


    /* move
    
        Méthode : 

            slide : permettra de faire passer les cases d'une ligne d'un côtés et de l'autre.
                - On concatene au tableau (Arr) le tableau de la valeur Zero.
    

            Chaque fois que l'on appuie sur une clé : 
                On parcours le tableau, et pour chaque ligne on appelle la méthode slide qui déplace nos 0.
    
    */ 
    public Case(int _valeur, int _id) {
        valeur = _valeur;
        id = _id;
    }

    /**
     * Procédure qui test si la surcharge de equals est valide. (remplis le contrat établi dans la norme pour les hashmaps)
     */
    public static void should_be_equals() throws Exception {
        final Case case1 = new Case(2, 6);
        final Case case2 = new Case(2, 6);
        final Case case3 = new Case(2, 6);

        // Test règle transitive
        assert case2.equals(case2);
        assert case3.equals(case3);

        // Test Symétrie
        assert (case1.equals(case2));
        assert (case2.equals(case1));

        // Transitive
        assert (case1.equals(case2));
        assert (case2.equals(case3));
        assert (case1.equals(case3));

    }

    /**
     *  Procédure static qui permet de comparer 2 cases entre elles et d'afficher un message qui indique leur relation.
     * @param case1 Case Source
     * @param case2 Case avec laquel on veut comparer la case source.
     */
    public static void equal_Case (Case case1, Case case2) {
        String output = case1.equals(case2)
                ? "Les cases sont les mêmes il y a un problème dans Equals."
                : "Les cases ne sont pas les mêmes niquel.";
        System.out.println(output);
    }

    public int getID() {
        return this.id;
    }
    public int getValeur() {
        return valeur;
    }
    public void setValeur(int _valeur) {
        this.valeur = _valeur;
    }

    /**
     * @param grille Référence du Jeu que l'on va modifier.
     * @param case1 Case que l'on va modifier.
     * @param case2 Case que l'on va modifier.
     * @param nouvelle_valeur1 La nouvelle valeur de la Case 1.
     * @param nouvelle_valeur2 La nouvelle valeur de la Case 2.
     */
    public static void change_cases(Jeu grille, Case case1, Case case2, int nouvelle_valeur1, int nouvelle_valeur2) {
        Case nouvelle_case1 = new Case(nouvelle_valeur1, case1.getID());
        Case nouvelle_case2 = new Case(nouvelle_valeur2, case2.getID());

        // Change la valeur du voisin dans le tableau et la hashmap.
        grille.setIndexCase(case1, nouvelle_case1);
        grille.setTabCases(nouvelle_case1, nouvelle_case1);
        // Peut causer un segfault car il faudrait savoir quand le garbage collector s'occupe de supprimer la case.
        // Normalement si on a bien fait notre boulot dans setIndexCase la nouvelle_case fait référence à l'ancienne.

        // On change aussi la valeur de la case actuelle.
        grille.setIndexCase(case2, nouvelle_case2);
        grille.setTabCases(nouvelle_case2, nouvelle_case2);
    }

    /**
     * Gère si la case courante doit fusionner avec sa case voisine et change les valeurs de ces dernières.
     * @param voisin Les coordonnées dans la grille de la case voisine.
     * @param moi Les coordonées de la case courante.
     * @param grille La grille dont on va modifier les cases.
     */
    private void fusion(Point voisin, Point moi, Jeu grille, int result) {
        Case case_voisin = grille.getCase(voisin);
        int valeur_voisin = grille.getCase(voisin).getValeur();

        if(this.valeur == valeur_voisin) {
            // Cas : Ma valeur est égale à celle de mon voisin, on fusionne et je passe à 0. exemple ( 2 * 2).
            change_cases(grille,case_voisin, this, valeur_voisin+this.valeur, 0);
            result += 1;
            grille.setCase_dispo();
        }
        else if(this.valeur != 0 && valeur_voisin == 0) {
            // Cas : Ma valeur est ¬null et mon voisin est null, je recopie ma valeur dans celle du voisin et je passe à 0.
            change_cases(grille,case_voisin, this, this.valeur, 0);
        }
        // On ne fait rien dans ces cas là.
        // Cas : Ma valeur est null et mon voisin aussi, on ne fait rien.
        // Cas : Ma valeur n'est pas la même que celle de mon voisin, on ne fait rien.
        // Cas : Ma valeur est null et mon voisin non. on ne fait rien.
    }

    /**
     * Fonction qui s'occupe de changer gérer le déplacement de la case courante, fait entre autre appel à la fusion.
     * @param voisin Point qui fait référence à la case voisine dans la grille.
     * @param grille La grille dont on veut changer les cases.
     */
    public void deplacer(Point voisin, Jeu grille, int result) {
        Point mesCoordonnees = grille.getCaseFromHash(this); // Je récupère mes coordonnées depuis la hashmap.
        // Si je ne suis pas égale à 0 je dois transmettre ma valeur ou fusionner avec voisin
        if(!(this.valeur == 0)) {
            fusion(voisin,mesCoordonnees,grille, result);
        }
    }

}
