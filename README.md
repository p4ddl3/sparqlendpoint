SparqlEndPoint
==============


MAJ du 24/07/2013
-----------------

- gestion du charmax comme parametre obligatoire pour chaque endpoint
- nouvelle gestion des endpoint, avec configurations persistantes
- sauvegarde d'une liste de prefixes pour chaque endpoint
- ajout de fonctionnalités sur le clic droit dans les résultats (parents, attributs, parents et attributs)
- ajout de raccourcis clavier (Ctrl+N pour un nouvel onglet, Echap pour fermer le champ de filtre)
- parallelisation des requetes : lorsque celles-ci sont découpés en plusieurs sous requêtes, les sous-requêtes sont exécutées via un pool de 20 thread.
- les requetes sont lancées en dehors de l'EDT (plus de freeze lors de grosses requetes)
- ajout d'une status bar, qui permet notamment d'afficher la progressions des requêtes




MAJ du 12/07/2013
-----------------

- Nouveau refactoring, hiérarchie du package view.
- Amélioration du gestionnaire de paramètres (ajout de la gestion des paramètres listés)
- Amélioration du debugger, pour visualiser le rendu des paramètres listés, ainsi que le découpage de la requête




MAJ du 08/07/2013
-----------------

- Gros refactoring sur le code source.
- Premier gestionnaire de parametres (uniquement les parametres atomiques pour le moment)
- mise en place d'une permiere version d'un debugger qui permet de visualiser la requete avec ces parametres.
- amelioration de la gestion des ajout/supp de tab
