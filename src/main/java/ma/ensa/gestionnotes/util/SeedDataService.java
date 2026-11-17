package ma.ensa.gestionnotes.util;

import java.util.Date;

import ma.ensa.gestionnotes.model.Etudiant;
import ma.ensa.gestionnotes.model.Professeur;
import ma.ensa.gestionnotes.model.Role;
import ma.ensa.gestionnotes.model.Utilisateur;
import ma.ensa.gestionnotes.service.EtudiantService;
import ma.ensa.gestionnotes.service.ProfesseurService;
import ma.ensa.gestionnotes.service.UtilisateurService;

public class SeedDataService {

    public void ensureDefaultData() {
        try {
            UtilisateurService utilisateurService = new UtilisateurService();
            if (!utilisateurService.trouverTout().isEmpty()) {
                return;
            }

            Utilisateur admin = new Utilisateur("admin", "admin123", "admin@ensa.ma", Role.ADMIN);
            utilisateurService.enregistrer(admin);

            Utilisateur professeurUser = new Utilisateur("professeur1", "prof123", "professeur1@ensa.ma", Role.PROFESSEUR);
            utilisateurService.enregistrer(professeurUser);

            Utilisateur etudiantUser = new Utilisateur("etudiant1", "etu123", "etudiant1@ensa.ma", Role.ETUDIANT);
            utilisateurService.enregistrer(etudiantUser);

            Professeur professeur = new Professeur("P-001", "Martin", "Alice", "Informatique", professeurUser);
            new ProfesseurService().enregistrer(professeur);

            Etudiant etudiant = new Etudiant("E-001", "Benali", "Yassine", new Date(), "M", etudiantUser);
            new EtudiantService().enregistrer(etudiant);
        } catch (Exception ignored) {
            // Seed data is optional when the local database is not available yet.
        }
    }
}
