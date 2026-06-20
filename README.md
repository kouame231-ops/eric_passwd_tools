# eric_passwd_tools

Outil en ligne de commande (CLI) écrit en Java 21, permettant de générer des mots de passe selon des critères personnalisables (longueur, types de caractères) et d'auditer leur robustesse réelle grâce à CrackLib, un utilitaire Linux standard de vérification de mots de passe, exécuté dans un conteneur Docker isolé.

Plutôt que de calculer lui-même un score de robustesse, le programme délègue cette validation à CrackLib : c'est le motif précis renvoyé par cet outil externe (mot du dictionnaire, motif trop simple, trop court, etc.) qui détermine le niveau final affiché à l'utilisateur, parmi cinq possibilités : Très faible, Faible, Moyen, Fort, Très fort.

## Architecture

Le projet est composé de deux parties indépendantes :

- une application Java 21 (package `com.passwdtools`), qui gère la génération des mots de passe et l'interaction avec l'utilisateur dans le terminal ;
- un conteneur Docker basé sur Debian, qui héberge uniquement le binaire `cracklib-check`, sans exposer aucun service réseau.

La communication entre les deux se fait via `docker exec`, piloté côté Java par la classe `ProcessBuilder`. Le mot de passe à auditer est transmis à `cracklib-check` via l'entrée standard du processus, et non en argument, afin d'éviter qu'il apparaisse dans la liste des processus du système.

## Prérequis

- GitHub Codespaces (Java et Docker sont déjà préconfigurés automatiquement à l'ouverture, grâce au fichier `.devcontainer/devcontainer.json` du projet), ou bien Docker et un JDK 21 installés localement.
- Git.

## Installation et exécution

### 1. Lancer le conteneur Docker

Depuis la racine du projet, là où se trouve `docker-compose.yml` :

```bash
docker compose up --build -d
```

Cette commande construit l'image (Debian + `cracklib-runtime`) et démarre un conteneur nommé `password-audit-cracklib`, qui reste actif en arrière-plan.

Pour vérifier que CrackLib répond correctement à l'intérieur du conteneur :

```bash
docker exec -i password-audit-cracklib cracklib-check <<< "test123"
```

### 2. Compiler l'application Java

```bash
cd src/main/java
javac --release 21 com/passwdtools/*.java -d ../../../out
```

L'option `--release 21` garantit que le code est compilé au niveau du langage Java 21, même si le JDK installé est une version plus récente.

### 3. Exécuter l'application

```bash
cd ../../../out
java com.passwdtools.Main
```

Le programme propose un menu interactif : longueur souhaitée, catégories de caractères à inclure (majuscules, minuscules, chiffres, symboles), et nombre de mots de passe à générer en une seule exécution. Chaque mot de passe généré est immédiatement audité via le conteneur Docker, et son niveau de robustesse est affiché.

### 4. Arrêter le conteneur

```bash
docker compose down
```

## Structure du projet

```
eric_passwd_tools/
├── .devcontainer/
│   └── devcontainer.json
├── docker/
│   └── Dockerfile
├── docker-compose.yml
├── src/main/java/com/passwdtools/
│   ├── Main.java
│   ├── PasswordConfig.java
│   ├── PasswordGenerator.java
│   ├── SecurityAuditClient.java
│   └── AuditResult.java
├── Documentation_Projet_eric_passwd_tools.docx
└── README.md
``` 