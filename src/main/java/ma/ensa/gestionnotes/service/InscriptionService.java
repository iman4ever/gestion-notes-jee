package ma.ensa.gestionnotes.service;

import java.util.List;

import ma.ensa.gestionnotes.dao.InscriptionDao;
import ma.ensa.gestionnotes.model.Inscription;

public class InscriptionService {

    private final InscriptionDao inscriptionDao = new InscriptionDao();

    public Inscription enregistrer(Inscription inscription) {
        valider(inscription);
        return inscriptionDao.save(inscription);
    }

    public Inscription modifier(Inscription inscription) {
        valider(inscription);
        return inscriptionDao.update(inscription);
    }

    public void supprimer(Inscription inscription) {
        inscriptionDao.delete(inscription);
    }

    public Inscription trouverParId(Long id) {
        return inscriptionDao.findById(id);
    }

    public List<Inscription> trouverTout() {
        return inscriptionDao.findAll();
    }

    public List<Inscription> trouverPourEtudiant(Long utilisateurId) {
        return inscriptionDao.findByEtudiantUtilisateurId(utilisateurId);
    }

    public List<Inscription> trouverPourPromotions(List<Long> promotionIds) {
        return inscriptionDao.findByPromotionIds(promotionIds);
    }

    private void valider(Inscription inscription) {
        if (inscription == null || inscription.getDateInscription() == null || inscription.getEtudiant() == null
                || inscription.getPromotion() == null) {
            throw new IllegalArgumentException("La date, l'étudiant et la promotion sont obligatoires.");
        }
        if (inscriptionDao.existsForEtudiantAndPromotion(inscription.getEtudiant().getId(),
                inscription.getPromotion().getId(), inscription.getId())) {
            throw new IllegalArgumentException("Cet étudiant est déjà inscrit à cette promotion.");
        }
    }
}
