package ma.ensa.gestionnotes.service;

import java.util.List;

import ma.ensa.gestionnotes.dao.PromotionDao;
import ma.ensa.gestionnotes.model.Promotion;

public class PromotionService {

    private final PromotionDao promotionDao = new PromotionDao();

    public Promotion enregistrer(Promotion promotion) {
        valider(promotion);
        return promotionDao.save(promotion);
    }

    public Promotion modifier(Promotion promotion) {
        valider(promotion);
        return promotionDao.update(promotion);
    }

    public void supprimer(Promotion promotion) {
        promotionDao.delete(promotion);
    }

    public Promotion trouverParId(Long id) {
        return promotionDao.findById(id);
    }

    public List<Promotion> trouverTout() {
        return promotionDao.findAll();
    }

    private void valider(Promotion promotion) {
        if (promotion == null || vide(promotion.getCode()) || vide(promotion.getLibelle())
                || promotion.getFiliere() == null || promotion.getNiveau() == null
                || promotion.getAnneeUniversitaire() == null) {
            throw new IllegalArgumentException("Une promotion doit avoir un code, un libellé, une filière, un niveau et une année.");
        }
    }

    private boolean vide(String value) {
        return value == null || value.isBlank();
    }
}
