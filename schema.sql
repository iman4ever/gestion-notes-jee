CREATE DATABASE IF NOT EXISTS gestion_notes;
USE gestion_notes;

CREATE TABLE IF NOT EXISTS utilisateurs (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  nom_utilisateur VARCHAR(100) NOT NULL UNIQUE,
  mot_de_passe VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL UNIQUE,
  role VARCHAR(30) NOT NULL,
  actif BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS etudiants (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  id_etudiant VARCHAR(50) NOT NULL UNIQUE,
  nom VARCHAR(100) NOT NULL,
  prenom VARCHAR(100) NOT NULL,
  date_naissance DATE,
  sexe VARCHAR(20) NOT NULL,
  utilisateur_id BIGINT NOT NULL UNIQUE,
  CONSTRAINT fk_etudiant_utilisateur FOREIGN KEY (utilisateur_id) REFERENCES utilisateurs(id)
);

CREATE TABLE IF NOT EXISTS professeurs (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  id_professeur VARCHAR(50) NOT NULL UNIQUE,
  nom VARCHAR(100) NOT NULL,
  prenom VARCHAR(100) NOT NULL,
  specialite VARCHAR(150) NOT NULL,
  utilisateur_id BIGINT NOT NULL UNIQUE,
  CONSTRAINT fk_professeur_utilisateur FOREIGN KEY (utilisateur_id) REFERENCES utilisateurs(id)
);

CREATE TABLE IF NOT EXISTS filieres (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  code VARCHAR(50) NOT NULL UNIQUE,
  libelle VARCHAR(150) NOT NULL
);

CREATE TABLE IF NOT EXISTS niveaux (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  code VARCHAR(50) NOT NULL UNIQUE,
  libelle VARCHAR(150) NOT NULL
);

CREATE TABLE IF NOT EXISTS annees_universitaires (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  libelle VARCHAR(100) NOT NULL,
  date_debut DATE NOT NULL,
  date_fin DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS promotions (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  code VARCHAR(50) NOT NULL UNIQUE,
  libelle VARCHAR(150) NOT NULL,
  filiere_id BIGINT NOT NULL,
  niveau_id BIGINT NOT NULL,
  annee_universitaire_id BIGINT NOT NULL,
  CONSTRAINT fk_promotion_filiere FOREIGN KEY (filiere_id) REFERENCES filieres(id),
  CONSTRAINT fk_promotion_niveau FOREIGN KEY (niveau_id) REFERENCES niveaux(id),
  CONSTRAINT fk_promotion_annee FOREIGN KEY (annee_universitaire_id) REFERENCES annees_universitaires(id)
);

CREATE TABLE IF NOT EXISTS modules (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  code VARCHAR(50) NOT NULL UNIQUE,
  libelle VARCHAR(150) NOT NULL,
  coefficient DOUBLE NOT NULL
);

CREATE TABLE IF NOT EXISTS inscriptions (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  date_inscription DATE NOT NULL,
  etudiant_id BIGINT NOT NULL,
  promotion_id BIGINT NOT NULL,
  CONSTRAINT fk_inscription_etudiant FOREIGN KEY (etudiant_id) REFERENCES etudiants(id),
  CONSTRAINT fk_inscription_promotion FOREIGN KEY (promotion_id) REFERENCES promotions(id)
);

CREATE TABLE IF NOT EXISTS enseignements (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  promotion_id BIGINT NOT NULL,
  module_id BIGINT NOT NULL,
  professeur_id BIGINT NOT NULL,
  CONSTRAINT fk_enseignement_promotion FOREIGN KEY (promotion_id) REFERENCES promotions(id),
  CONSTRAINT fk_enseignement_module FOREIGN KEY (module_id) REFERENCES modules(id),
  CONSTRAINT fk_enseignement_professeur FOREIGN KEY (professeur_id) REFERENCES professeurs(id)
);

CREATE TABLE IF NOT EXISTS notes (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  valeur DOUBLE NOT NULL,
  date_saisie DATE NOT NULL,
  inscription_id BIGINT NOT NULL,
  enseignement_id BIGINT NOT NULL,
  CONSTRAINT fk_note_inscription FOREIGN KEY (inscription_id) REFERENCES inscriptions(id),
  CONSTRAINT fk_note_enseignement FOREIGN KEY (enseignement_id) REFERENCES enseignements(id)
);
