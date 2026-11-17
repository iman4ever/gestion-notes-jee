package ma.ensa.gestionnotes.dao;

import java.util.List;

import org.hibernate.Session;

import ma.ensa.gestionnotes.config.HibernateUtil;
import ma.ensa.gestionnotes.model.Promotion;

public class PromotionDao extends GenericDao<Promotion> {
    public PromotionDao() {
        super(Promotion.class);
    }

    @Override
    public Promotion findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select p from Promotion p left join fetch p.filiere left join fetch p.niveau "
                            + "left join fetch p.anneeUniversitaire where p.id = :id",
                    Promotion.class).setParameter("id", id).uniqueResult();
        }
    }

    @Override
    public List<Promotion> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select p from Promotion p left join fetch p.filiere left join fetch p.niveau "
                            + "left join fetch p.anneeUniversitaire order by p.libelle",
                    Promotion.class).list();
        }
    }
}
