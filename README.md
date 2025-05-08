# GridSim - Simulation Distribuée de Multiplication Matricielle

Ce dépôt présente un projet de simulation de multiplication matricielle distribuée avec **GridSim**, illustrant l'application du parallélisme dans un environnement simulé de grille informatique.

---

## Objectif du projet

Réaliser une multiplication matricielle `C = A × B` dans un environnement distribué simulé, où chaque coefficient `C[i][j]` est calculé par une unité indépendante (« Gridlet »).

---

## Approche choisie

Chaque Gridlet calcule **un seul coefficient** `C[i][j]` de la matrice résultat.

* Reçoit la ligne `i` de A et la colonne `j` de B
* Calcule le produit scalaire
* Renvoie le résultat à l'utilisateur

> ✳ Ce choix maximise le parallélisme et simplifie la logique du code.

---

## Topologie réseau

Architecture hiérarchique en 3 couches :

1. **Tier 1 (Accès)** : Utilisateurs connectés à `r1`
2. **Tier 2 (Cœur)** : `r1` connecté à `r2`
3. **Ressources** : `r2` connecté à plusieurs `MyResource`

Tous les liens sont à **10 Mb/s**.

---

## Entités principales

* **NetUser** : gère la création et l'envoi des Gridlets
* **MyGridlet** : encapsule une ligne, une colonne et le résultat scalaire
* **MyResource** : traite les Gridlets et renvoie les résultats
* **Router (r1, r2)** : dirigent les Gridlets vers les ressources
* **SimpleLink** : spécifie bande passante et délais

---

## Flux de communication

Cycle de vie d'un Gridlet, de sa création à son traitement :

![Diagramme de flux](https://github.com/Yanis-mnh/gridsim-matrix-multiplication/blob/main/example/flowDiagrameGI.drawio.png)

---

## Implémentation (extraits)

**NetUser.java** :

```java
MyGridlet gl = new MyGridlet(id, ... , matAL , matBC);
gl.setUserID(userID);
this.list_.add(gl);
```

**MyGridlet.java** :

```java
public MyGridlet(int id, ..., ArrayList<Float> ligne, ArrayList<Float> column) {
    super(id , ...);
    this.ligne = ligne;
    this.column = column;
}
```

**MyResource.java** :

```java
protected void processOtherEvent(Sim_event ev) {
    if (ev.get_tag() == MY_GRIDLET_TAG) {
        MyGridlet gl = (MyGridlet) ev.get_data();
        processGridlet(gl);
    }
}
```

## Conclusion

Cette simulation met en œuvre une stratégie efficace de parallélisation par découpage fin. Le modèle réseau, la structure du code et l'utilisation de GridSim permettent d'observer les impacts de la charge réseau et du parallélisme massif dans une architecture distribuée.
