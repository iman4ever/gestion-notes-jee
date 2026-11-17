package ma.ensa.gestionnotes.service;

import java.util.List;

import ma.ensa.gestionnotes.dao.FiliereDao;
import ma.ensa.gestionnotes.model.Filiere;

public class FiliereService {

    private final FiliereDao filiereDao = new FiliereDao();

    public Filiere enregistrer(Filiere filiere) {
        return filiereDao.save(filiere);
    }

    public Filiere modifier(Filiere filiere) {
        return filiereDao.update(filiere);
    }

    public void supprimer(Filiere filiere) {
        filiereDao.delete(filiere);
    }

    public Filiere trouverParId(Long id) {
        return filiereDao.findById(id);
    }

    public List<Filiere> trouverTout() {
        return filiereDao.findAll();
    }
}
