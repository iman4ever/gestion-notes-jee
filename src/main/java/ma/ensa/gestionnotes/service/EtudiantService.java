package ma.ensa.gestionnotes.service;

import java.util.List;

import ma.ensa.gestionnotes.dao.EtudiantDao;
import ma.ensa.gestionnotes.dao.UtilisateurDao;
import ma.ensa.gestionnotes.model.Etudiant;

public class EtudiantService {

    private final EtudiantDao etudiantDao = new EtudiantDao();
    private final UtilisateurDao utilisateurDao = new UtilisateurDao();

    public Etudiant enregistrer(Etudiant etudiant) {
        valider(etudiant, null);
        return etudiantDao.save(etudiant);
    }

    public Etudiant modifier(Etudiant etudiant) {
        valider(etudiant, etudiant.getId());
        return etudiantDao.update(etudiant);
    }

    public void supprimer(Etudiant etudiant) {
        etudiantDao.delete(etudiant);
    }

    public Etudiant trouverParId(Long id) {
        return etudiantDao.findById(id);
    }

    public List<Etudiant> trouverTout() {
        return etudiantDao.findAll();
    }

    public Etudiant trouverParUtilisateurId(Long utilisateurId) {
        return etudiantDao.findByUtilisateurId(utilisateurId);
    }

    private void valider(Etudiant etudiant, Long excludedId) {
        if (etudiant == null || vide(etudiant.getIdEtudiant()) || vide(etudiant.getNom())
                || vide(etudiant.getPrenom()) || vide(etudiant.getSexe()) || etudiant.getUtilisateur() == null) {
            throw new IllegalArgumentException("Tous les champs obligatoires de l'étudiant doivent être renseignés.");
        }
        etudiant.setIdEtudiant(etudiant.getIdEtudiant().trim());
        etudiant.setNom(etudiant.getNom().trim());
        etudiant.setPrenom(etudiant.getPrenom().trim());
        etudiant.setSexe(etudiant.getSexe().trim());
        if (!"M".equalsIgnoreCase(etudiant.getSexe()) && !"F".equalsIgnoreCase(etudiant.getSexe())) {
            throw new IllegalArgumentException("Le sexe doit être renseigné par M ou F.");
        }
        if (etudiant.getDateNaissance() != null && etudiant.getDateNaissance().after(new java.util.Date())) {
            throw new IllegalArgumentException("La date de naissance ne peut pas être dans le futur.");
        }
        if (etudiantDao.existsByCne(etudiant.getIdEtudiant().trim(), excludedId)) {
            throw new IllegalArgumentException("Ce CNE est déjà attribué à un autre étudiant.");
        }
        if (vide(etudiant.getUtilisateur().getNomUtilisateur()) || vide(etudiant.getUtilisateur().getEmail())
                || vide(etudiant.getUtilisateur().getMotDePasse())) {
            throw new IllegalArgumentException("Le nom d'utilisateur, l'email et le mot de passe sont obligatoires.");
        }
        if (!emailValide(etudiant.getUtilisateur().getEmail())) {
            throw new IllegalArgumentException("L'adresse email de l'étudiant n'est pas valide.");
        }
        etudiant.getUtilisateur().setNomUtilisateur(etudiant.getUtilisateur().getNomUtilisateur().trim());
        etudiant.getUtilisateur().setEmail(etudiant.getUtilisateur().getEmail().trim());
        Long userId = etudiant.getUtilisateur().getId();
        if (utilisateurDao.existsByUsername(etudiant.getUtilisateur().getNomUtilisateur().trim(), userId)) {
            throw new IllegalArgumentException("Ce nom d'utilisateur est déjà utilisé.");
        }
        if (utilisateurDao.existsByEmail(etudiant.getUtilisateur().getEmail().trim(), userId)) {
            throw new IllegalArgumentException("Cette adresse email est déjà utilisée.");
        }
    }

    private boolean vide(String valeur) {
        return valeur == null || valeur.isBlank();
    }

    private boolean emailValide(String email) {
        return email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    }
}
