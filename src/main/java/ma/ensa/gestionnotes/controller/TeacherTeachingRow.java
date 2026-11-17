package ma.ensa.gestionnotes.controller;

import ma.ensa.gestionnotes.model.Enseignement;

public class TeacherTeachingRow {
    private final Enseignement enseignement;
    private final int nombreEtudiants;

    public TeacherTeachingRow(Enseignement enseignement, int nombreEtudiants) {
        this.enseignement = enseignement;
        this.nombreEtudiants = nombreEtudiants;
    }

    public Enseignement getEnseignement() {
        return enseignement;
    }

    public int getNombreEtudiants() {
        return nombreEtudiants;
    }
}
