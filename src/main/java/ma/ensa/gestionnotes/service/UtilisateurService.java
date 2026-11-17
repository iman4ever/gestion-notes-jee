package ma.ensa.gestionnotes.service;

import java.util.List;

import ma.ensa.gestionnotes.dao.UtilisateurDao;
import ma.ensa.gestionnotes.model.Role;
import ma.ensa.gestionnotes.model.Utilisateur;

public class UtilisateurService {

    private final UtilisateurDao utilisateurDao = new UtilisateurDao();

    public Utilisateur enregistrer(Utilisateur utilisateur) {
        return utilisateurDao.save(utilisateur);
    }

    public Utilisateur modifier(Utilisateur utilisateur) {
        return utilisateurDao.update(utilisateur);
    }

    public void supprimer(Utilisateur utilisateur) {
        utilisateurDao.delete(utilisateur);
    }

    public Utilisateur trouverParId(Long id) {
        return utilisateurDao.findById(id);
    }

    public List<Utilisateur> trouverTout() {
        return utilisateurDao.findAll();
    }

    public Utilisateur authentifier(String nomUtilisateur, String motDePasse) {
        Utilisateur utilisateur = utilisateurDao.findByUsername(nomUtilisateur);
        if (utilisateur != null && utilisateur.getMotDePasse().equals(motDePasse) && utilisateur.isActif()) {
            return utilisateur;
        }
        return null;
    }

    public List<Utilisateur> trouverParRole(Role role) {
        return utilisateurDao.findByRole(role);
    }
}
