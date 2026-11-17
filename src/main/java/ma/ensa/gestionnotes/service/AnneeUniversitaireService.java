package ma.ensa.gestionnotes.service;

import java.util.List;

import ma.ensa.gestionnotes.dao.AnneeUniversitaireDao;
import ma.ensa.gestionnotes.model.AnneeUniversitaire;

public class AnneeUniversitaireService {

    private final AnneeUniversitaireDao anneeUniversitaireDao = new AnneeUniversitaireDao();

    public AnneeUniversitaire enregistrer(AnneeUniversitaire anneeUniversitaire) {
        valider(anneeUniversitaire);
        return anneeUniversitaireDao.save(anneeUniversitaire);
    }

    public AnneeUniversitaire modifier(AnneeUniversitaire anneeUniversitaire) {
        valider(anneeUniversitaire);
        return anneeUniversitaireDao.update(anneeUniversitaire);
    }

    public void supprimer(AnneeUniversitaire anneeUniversitaire) {
        anneeUniversitaireDao.delete(anneeUniversitaire);
    }

    public AnneeUniversitaire trouverParId(Long id) {
        return anneeUniversitaireDao.findById(id);
    }

    public List<AnneeUniversitaire> trouverTout() {
        return anneeUniversitaireDao.findAll();
    }

    private void valider(AnneeUniversitaire annee) {
        if (annee == null || annee.getLibelle() == null || annee.getLibelle().isBlank()
                || annee.getDateDebut() == null || annee.getDateFin() == null
                || annee.getDateFin().before(annee.getDateDebut())) {
            throw new IllegalArgumentException("L'année doit avoir un libellé et des dates valides.");
        }
    }
}
