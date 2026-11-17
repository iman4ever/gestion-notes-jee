package ma.ensa.gestionnotes.service;

import java.util.List;

import ma.ensa.gestionnotes.dao.EnseignementDao;
import ma.ensa.gestionnotes.model.Enseignement;

public class EnseignementService {

    private final EnseignementDao enseignementDao = new EnseignementDao();

    public Enseignement enregistrer(Enseignement enseignement) {
        valider(enseignement);
        return enseignementDao.save(enseignement);
    }

    public Enseignement modifier(Enseignement enseignement) {
        valider(enseignement);
        return enseignementDao.update(enseignement);
    }

    public void supprimer(Enseignement enseignement) {
        enseignementDao.delete(enseignement);
    }

    public Enseignement trouverParId(Long id) {
        return enseignementDao.findById(id);
    }

    public List<Enseignement> trouverTout() {
        return enseignementDao.findAll();
    }

    public List<Enseignement> trouverPourProfesseur(Long utilisateurId) {
        return enseignementDao.findByProfesseurUtilisateurId(utilisateurId);
    }

    public List<Enseignement> trouverPourPromotions(List<Long> promotionIds) {
        return enseignementDao.findByPromotionIds(promotionIds);
    }

    private void valider(Enseignement enseignement) {
        if (enseignement == null || enseignement.getPromotion() == null || enseignement.getModule() == null
                || enseignement.getProfesseur() == null) {
            throw new IllegalArgumentException("Un enseignement doit associer une promotion, un module et un professeur.");
        }
    }
}
