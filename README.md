# Ressources utiles Swing


https://jmdoudoux.developpez.com/cours/developpons/java/chap-swing.php


# Modèle 
 
Classe : Jeu et Case, Enum.

## Case

Dans la fonction move, on bouge les cases (on passe la référence à une autre)

Pour bouger une case : 
    - On interroge le Jeu, si on va vers la gauche par exemple, on regarde si la case est nulle ou si on peut fusionner avec. 
    - checker si la fusion est null ou non pour un cas d'erreur.


# Fin de partie 

Test, lorsque l'on a terminé le tour et que l'on va générer une nouvelle valeur dans le tableau, si on peut pas mettre de nouvelle case terminé.
# Remarque sur certains éléments

Lorsque l'on change la valeur d'une case via le tableau ou Hashmap, comme c'est une référence
la valeur est changé aussi bien dans le tableau que la hashmap.
