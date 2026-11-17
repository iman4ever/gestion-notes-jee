package ma.ensa.gestionnotes.dao;

import java.util.List;

import org.hibernate.Session;

import ma.ensa.gestionnotes.config.HibernateUtil;
import ma.ensa.gestionnotes.model.Etudiant;

public class EtudiantDao extends GenericDao<Etudiant> {
    public EtudiantDao() {
        super(Etudiant.class);
    }

    public Etudiant findByUtilisateurId(Long utilisateurId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "select e from Etudiant e join fetch e.utilisateur where e.utilisateur.id = :id",
                            Etudiant.class)
                    .setParameter("id", utilisateurId).uniqueResult();
        }
    }

    @Override
    public Etudiant findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("select e from Etudiant e join fetch e.utilisateur where e.id = :id",
                    Etudiant.class).setParameter("id", id).uniqueResult();
        }
    }

    public boolean existsByCne(String cne, Long excludedId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long count = session.createQuery(
                            "select count(e.id) from Etudiant e where lower(e.idEtudiant) = lower(:cne) "
                                    + "and (:excludedId is null or e.id <> :excludedId)", Long.class)
                    .setParameter("cne", cne).setParameter("excludedId", excludedId).uniqueResult();
            return count != null && count > 0;
        }
    }

    @Override
    public List<Etudiant> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("select e from Etudiant e join fetch e.utilisateur order by e.nom, e.prenom",
                    Etudiant.class).list();
        }
    }
}
