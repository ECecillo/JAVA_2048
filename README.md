# Ressources utiles Swing


https://jmdoudoux.developpez.com/cours/developpons/java/chap-swing.php


# Design Pattern : Observer

La classe Jeu est l'Objet qui est observé par les classes Swing et Console, on utilise la méthode setChanged 
pour les notifiers qu'il y a eu un changement dans le modele et que la vue doit se rafraichir.

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


# Algorithme du Jeu

Le jeu est basé sur deux fonctions, action et deplacer qui sont respectivement dans les classes
Jeu et Case.

## Logique 

Pour fusionner les cases et additionner leur valeur on doit regarder de quel côté on va :
___
- Vers la droite : j'additionne les valeurs de droite à gauche et combler le vide de gauche à droite.
- Vers la gauche : j'additionne les valeurs de gauche à droite et on comble le vide de droite à gauche.
- Vers le haut : j'additionne les valeurs de bas en haut et comble le vide de haut en bas.
- Vers le bas : j'aditionne les valeurs de haut en bas et comble le vide bas en haut.

Pour le moment, ce jeu ne fait pas l'addition dans ce sens mais dans le sens du déplacement de la grille.

### Procédure action(Direction direction)
Fonction de Controle qui va s'occuper d'appeler toutes les cases pour qu'elles s'échangent ou fusionnent leurs valeurs
avec leurs voisins.

#### Algorihtme action
___
```aidl
Debut action
    Selon la direction vers laquelle on va :
    |     Initialise : direction_colonne/ligne, l'indice de colonne/ligne de départ et indice colonne/ligne d'arrivé.
    Fin Selon.
    
    Pour i allant de ligne_depart à ligne_arrivee pas 1 :
    |    Pour j allant de colonne_depart à colonne_arrive pas 1 :
    |     |   On prend la valeur absolue au cas où on a un chiffre négatif.
    |     |   On calcule la position du voisin en fonction de la valeur absolue pour que l'indice soit valide.
    |     |   Si l'indice du voisin est bien dans la grille :
    |     |    |   deplacer(voisin, case, grille)
    |     |   FinSi
    |    Fin Pour j
    Fin Pour i
    
    On ajout une nouvelle valeur aléatoire dans la grille.
Fin action 
```

#### Gestion Direction
___
Comme on connait la direction, on doit gérer comment on va parcourir le tableau de
case et savoir où se trouve le voisin de la case que l'on regarde.

On distingue 2 cas : 
___
- **Droite, Bas** : On boucle de `0 à taille_jeu` et on incrémente `i` et `j` de 1.
- **Gauche, Haut** : On boucle de `-(taille_jeu) à 0` et on incrémente `i` et `j` de 1. 

##### Explications Gauche et Haut
___

Pour éviter de devoir faire une boucle pour chaque cas, on doit juste faire en sorte que lorsque 
l'on incrémente on va de l'autre côté. 

Pour ce faire, on a simplement besoin d'incrémenter un chiffre négatif et prendre la valeur absolue 
de ce dernier, pour avoir un indice valide dans notre tableau de case.

Enfin, comme on part des extrémités du tableau par exemple avec une grille 4x4 on partira de `4 - 1` pour un indice valide
d'où le `(i + 1) && (j + 1)` dans le code de la fonction `action`.

### Procédure Case.deplacer(Point voisin, Jeu grille)
___

Déclaré dans la classe Case, cette dernière permet à une case de percevoir
son voisinage, se déplacer et fusionner (en cas de comptabilité) sinon on ne change pas ces dernières.

L'algo est assez simple il suffit de gérer les cas suivants : 
- Si la valeur de la case courante, est égale à celle de son voisin, on fusionne avec voisin et passe case courante à 0.
- Si la valeur de la case courante != 0 et le voisin est égale à 0, on fusionne avec voisin et passe case courante à 0.
- Dans les autres cas, on ne fait rien.

### Remarques 