package ma.ensa.gestionnotes.service;

import java.util.List;

import ma.ensa.gestionnotes.dao.NiveauDao;
import ma.ensa.gestionnotes.model.Niveau;

public class NiveauService {

    private final NiveauDao niveauDao = new NiveauDao();

    public Niveau enregistrer(Niveau niveau) {
        return niveauDao.save(niveau);
    }

    public Niveau modifier(Niveau niveau) {
        return niveauDao.update(niveau);
    }

    public void supprimer(Niveau niveau) {
        niveauDao.delete(niveau);
    }

    public Niveau trouverParId(Long id) {
        return niveauDao.findById(id);
    }

    public List<Niveau> trouverTout() {
        return niveauDao.findAll();
    }
}
