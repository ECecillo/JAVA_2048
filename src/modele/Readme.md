# class Jeu 

`IndexCase` : Hash Table dans laquelle on va stocker les indices de chaque case du tableau, pour les retrouver en O(1) lorsque l'on a besoin de consulter les voisins d'une case.

`Case[][]` : Tableau dans lequelle on va modifier les cases.

`Interface Executor` : Pool de processus. Au lieu d'utiliser les fonctionnalités de la classe `Thread`, on crée une variable utilisable par l'Executor: `ExecutorService service = Executors.newSingleThreadExecutor();`

Puis on execute avec `service.execute( new Runnable() { public void run(){...}};`

# class Case

### `Déplacer(Direction, Jeu)`

#### Résumé

Fonction qui permet à une case de regarder/percevoir son voisinage, elle peut fusionner avec les cases voisines ou se déplacer si la case est NULL.

### Comment ça marche ?

Lorsque l'on apelle la fonction on lui passe une direction qui correspond à un élément de l'`enum Direction` : `haut, bas , gauche , droite`.

On lui passe également une référence à l'Objet `Jeu`, pour que cette dernière puisse récupérer sa position dans `IndexCase`.

On doit faire en sorte que : 
#### Déroulé Algo pour le cas Haut.
En partant de la dernière ligne (c.-à-d. 3 dans le cas du 2048).

`i` = position de la case à la ligne i.

`a_change = false` = boolean qui va nous permettre de savoir si la case a changé.

- On regarde la case `i - 1` et on vérifie qu'elle n'est pas inférieur à 0 :
  - `Si` la case voisine est non NULL je fusionne : 
    - **Si** ma valeur est égale à cette dernière
      - `valeur case i-1 * 2` et `a_change = true`.
  - `Sinon Si` elle est `NULL` : 
    - Je `i-1 = valeur de case i` et `a_change = true`.
  - Si `a_change = true` 
    - `i = null`, on a bien récopié ou add avec la case à côté.

#### Cas pour Bas, Droite et Gauche

Vous aurez sans doute compris que l'on va effectuer les mêmes opérations à l'exception que l'on va devoir changer l'ordre du traitement des éléments.

- Bas : `i + 1` pour les voisins en partant de `i = 0`.
- Gauche : `j - 1` en partant de `j = 3`.
- Droite : `j + 1` en partant de `j + 1`.

### Remarques et Solutions

On remarque un certains pattern entre (**Haut** && **Gauche**) et (**Bas** && **Droite**).

La question étant : Comment faire pour que l'on sache, si on doit `incrémenter` ou `décrémenter` ?
(ßønn€ qu€sŧiøn)

#### Première Solution Brute

On pourrait passer en paramètre des fonctions soit un entier avec la valeur 3 ou 0.

- Si on est à 3 on sait que l'on doit décrémenter jusqu'à 0.
- Si on est à 0 on sait que l'on doit décrémenter.

# Détails 

Processus métier, toutes les classes que l'on utilise pour faire du traitement de donnée et ou produire des choses pour la vue.
