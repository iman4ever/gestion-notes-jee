package ma.ensa.gestionnotes.dao;

import java.util.List;

import org.hibernate.Session;

import ma.ensa.gestionnotes.config.HibernateUtil;
import ma.ensa.gestionnotes.model.Enseignement;

public class EnseignementDao extends GenericDao<Enseignement> {
    public EnseignementDao() {
        super(Enseignement.class);
    }

    @Override
    public Enseignement findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select e from Enseignement e join fetch e.promotion join fetch e.module "
                            + "join fetch e.professeur where e.id = :id",
                    Enseignement.class).setParameter("id", id).uniqueResult();
        }
    }

    @Override
    public List<Enseignement> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select e from Enseignement e join fetch e.promotion join fetch e.module "
                            + "join fetch e.professeur p join fetch p.utilisateur order by e.promotion.libelle",
                    Enseignement.class).list();
        }
    }

    public List<Enseignement> findByProfesseurUtilisateurId(Long utilisateurId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select e from Enseignement e join fetch e.promotion promo "
                            + "join fetch promo.filiere join fetch promo.niveau "
                            + "join fetch promo.anneeUniversitaire join fetch e.module "
                            + "join fetch e.professeur p join fetch p.utilisateur u "
                            + "where u.id = :id order by e.promotion.libelle",
                    Enseignement.class).setParameter("id", utilisateurId).list();
        }
    }

    public List<Enseignement> findByPromotionIds(List<Long> promotionIds) {
        if (promotionIds.isEmpty()) {
            return List.of();
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select e from Enseignement e join fetch e.promotion p join fetch e.module "
                            + "where p.id in (:promotionIds) order by p.libelle, e.module.libelle",
                    Enseignement.class).setParameterList("promotionIds", promotionIds).list();
        }
    }
}
