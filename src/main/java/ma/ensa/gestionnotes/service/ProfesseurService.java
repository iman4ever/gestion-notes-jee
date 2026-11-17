package ma.ensa.gestionnotes.service;

import java.util.List;

import ma.ensa.gestionnotes.dao.ProfesseurDao;
import ma.ensa.gestionnotes.dao.UtilisateurDao;
import ma.ensa.gestionnotes.model.Professeur;

public class ProfesseurService {

    private final ProfesseurDao professeurDao = new ProfesseurDao();
    private final UtilisateurDao utilisateurDao = new UtilisateurDao();

    public Professeur enregistrer(Professeur professeur) {
        valider(professeur);
        return professeurDao.save(professeur);
    }

    public Professeur modifier(Professeur professeur) {
        valider(professeur);
        return professeurDao.update(professeur);
    }

    public void supprimer(Professeur professeur) {
        professeurDao.delete(professeur);
    }

    public Professeur trouverParId(Long id) {
        return professeurDao.findById(id);
    }

    public List<Professeur> trouverTout() {
        return professeurDao.findAll();
    }

    public Professeur trouverParUtilisateurId(Long utilisateurId) {
        return professeurDao.findByUtilisateurId(utilisateurId);
    }

    private void valider(Professeur professeur) {
        if (professeur == null || vide(professeur.getIdProfesseur()) || vide(professeur.getNom())
                || vide(professeur.getPrenom()) || vide(professeur.getSpecialite())
                || professeur.getUtilisateur() == null || vide(professeur.getUtilisateur().getNomUtilisateur())
                || vide(professeur.getUtilisateur().getEmail()) || vide(professeur.getUtilisateur().getMotDePasse())) {
            throw new IllegalArgumentException("Tous les champs obligatoires du professeur doivent être renseignés.");
        }
        professeur.setIdProfesseur(professeur.getIdProfesseur().trim());
        professeur.setNom(professeur.getNom().trim());
        professeur.setPrenom(professeur.getPrenom().trim());
        professeur.setSpecialite(professeur.getSpecialite().trim());
        if (!professeur.getUtilisateur().getEmail().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new IllegalArgumentException("L'adresse email du professeur n'est pas valide.");
        }
        professeur.getUtilisateur().setNomUtilisateur(professeur.getUtilisateur().getNomUtilisateur().trim());
        professeur.getUtilisateur().setEmail(professeur.getUtilisateur().getEmail().trim());
        Long userId = professeur.getUtilisateur().getId();
        if (utilisateurDao.existsByUsername(professeur.getUtilisateur().getNomUtilisateur().trim(), userId)) {
            throw new IllegalArgumentException("Ce nom d'utilisateur est déjà utilisé.");
        }
        if (utilisateurDao.existsByEmail(professeur.getUtilisateur().getEmail().trim(), userId)) {
            throw new IllegalArgumentException("Cette adresse email est déjà utilisée.");
        }
    }

    private boolean vide(String valeur) {
        return valeur == null || valeur.isBlank();
    }
}
