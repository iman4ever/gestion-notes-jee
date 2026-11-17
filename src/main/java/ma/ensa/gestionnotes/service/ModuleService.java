package ma.ensa.gestionnotes.service;

import java.util.List;

import ma.ensa.gestionnotes.dao.ModuleDao;
import ma.ensa.gestionnotes.model.Module;

public class ModuleService {

    private final ModuleDao moduleDao = new ModuleDao();

    public Module enregistrer(Module module) {
        valider(module);
        return moduleDao.save(module);
    }

    public Module modifier(Module module) {
        valider(module);
        return moduleDao.update(module);
    }

    public void supprimer(Module module) {
        moduleDao.delete(module);
    }

    public Module trouverParId(Long id) {
        return moduleDao.findById(id);
    }

    public List<Module> trouverTout() {
        return moduleDao.findAll();
    }

    private void valider(Module module) {
        if (module == null || module.getCode() == null || module.getCode().isBlank()
                || module.getLibelle() == null || module.getLibelle().isBlank()
                || !Double.isFinite(module.getCoefficient()) || module.getCoefficient() <= 0) {
            throw new IllegalArgumentException("Le code, le libellé et un coefficient positif sont obligatoires.");
        }
    }
}
