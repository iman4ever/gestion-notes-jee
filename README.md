# Gestion des notes et des résultats

## Présentation du projet

Ce projet a été réalisé dans le cadre du module **Architecture JEE**, en deuxieme année du cycle ingénieur, filière **Développement Logiciel et Applicatif (DLA)** à l'ENSA Agadir.

Il consiste à développer une application web permettant de gérer les notes et les résultats des étudiants.

L'application propose trois espaces différents : administrateur, professeur et étudiant. Chaque utilisateur accède aux fonctionnalités correspondant à son rôle.

## Réalisé par

- **Imane EN-NAIMY**
- **Farida GARROUJ**

**Encadrante :** Mme Bnider Zahra

**Établissement :** École Nationale des Sciences Appliquées d'Agadir

**Année universitaire :** 2026–2027

## Technologies utilisées

- Java
- Jakarta EE (Servlets et JSP)
- Hibernate
- MySQL
- Maven
- HTML et CSS
- Apache Tomcat
- Git et GitHub

L'application est organisée selon l'architecture MVC, avec des classes DAO et Service pour la gestion des données et des traitements.

## Fonctionnalités de l'application

### Administrateur

L'administrateur peut gérer les différentes informations de l'application :

- Les étudiants et les professeurs
- Les filières et les niveaux
- Les années universitaires et les promotions
- Les modules et leurs coefficients
- Les inscriptions des étudiants
- Les affectations des enseignements aux professeurs

### Professeur

Le professeur peut consulter les modules qui lui sont attribués et gérer les notes des étudiants inscrits dans ses enseignements.

Il peut ajouter, modifier et consulter les notes.

### Étudiant

L'étudiant peut accéder à son espace personnel pour consulter :

- Ses informations académiques
- Les modules de sa promotion
- Ses notes
- Sa moyenne générale
- Son résultat final

La moyenne est calculée en tenant compte des coefficients des modules.

## Base de données

Les données de l'application sont enregistrées dans une base MySQL appelée `gestion_notes`.

Hibernate est utilisé pour assurer la communication entre les classes Java et les tables de la base de données.

## Installation et exécution

Pour exécuter le projet :

1. Récupérer le projet depuis GitHub.
2. Créer une base de données MySQL nommée `gestion_notes`.
3. Copier `src/main/resources/hibernate.cfg.example.xml` vers `src/main/resources/hibernate.cfg.xml`, puis configurer les paramètres de connexion MySQL.
4. Ouvrir le projet dans IntelliJ IDEA.
5. Exécuter la commande :

```bash
mvn clean package
```

6. Déployer l'application sur Apache Tomcat 10.1.
7. Accéder à l'application depuis le navigateur.

## Modélisation UML

La conception de l'application repose sur trois types de diagrammes UML :

- Diagramme de cas d'utilisation
- Diagramme de classes
- Diagramme de séquence

Ces diagrammes permettent de représenter les fonctionnalités du système, les relations entre les classes et les interactions entre les différents composants.

## Conclusion

Ce projet nous a permis de mettre en pratique les notions étudiées dans le module Architecture JEE, notamment les Servlets, JSP, Hibernate, l'architecture MVC et la gestion d'une base de données relationnelle.

Il nous a également permis de nous familiariser avec Git et GitHub pour le partage du code et le travail en binôme.
