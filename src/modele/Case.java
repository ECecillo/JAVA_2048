package modele;

import java.awt.*;
import java.util.Objects;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
    @Override
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
    }

    public Case(int _valeur, int _id) {
        valeur = _valeur;
        id = _id;
    }
    // Procédure qui test si la surcharge de equals est valide.
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



    public int getID() {
        return this.id;
    }
    public int getValeur() {
        return valeur;
    }

    public void setValeur(int _valeur) {
        this.valeur = _valeur;
    }

    // Jeu.hashMap.getValue(this)
}
