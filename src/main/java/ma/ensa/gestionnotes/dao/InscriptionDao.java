package ma.ensa.gestionnotes.dao;

import java.util.List;

import org.hibernate.Session;

import ma.ensa.gestionnotes.config.HibernateUtil;
import ma.ensa.gestionnotes.model.Inscription;

public class InscriptionDao extends GenericDao<Inscription> {
    public InscriptionDao() {
        super(Inscription.class);
    }

    @Override
    public Inscription findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select i from Inscription i join fetch i.etudiant e join fetch e.utilisateur "
                            + "join fetch i.promotion where i.id = :id",
                    Inscription.class).setParameter("id", id).uniqueResult();
        }
    }

    @Override
    public List<Inscription> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select i from Inscription i join fetch i.etudiant e join fetch e.utilisateur "
                            + "join fetch i.promotion order by i.dateInscription desc",
                    Inscription.class).list();
        }
    }

    public List<Inscription> findByEtudiantUtilisateurId(Long utilisateurId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select i from Inscription i join fetch i.etudiant e join fetch e.utilisateur "
                            + "join fetch i.promotion p join fetch p.filiere join fetch p.niveau "
                            + "join fetch p.anneeUniversitaire where e.utilisateur.id = :id "
                            + "order by i.dateInscription desc",
                    Inscription.class).setParameter("id", utilisateurId).list();
        }
    }

    public List<Inscription> findByPromotionIds(List<Long> promotionIds) {
        if (promotionIds.isEmpty()) {
            return List.of();
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select distinct i from Inscription i join fetch i.etudiant e join fetch e.utilisateur "
                            + "join fetch i.promotion p join fetch p.filiere join fetch p.niveau "
                            + "join fetch p.anneeUniversitaire where p.id in (:promotionIds) "
                            + "order by p.libelle, e.nom, e.prenom",
                    Inscription.class).setParameterList("promotionIds", promotionIds).list();
        }
    }

    public boolean existsForEtudiantAndPromotion(Long etudiantId, Long promotionId, Long excludedId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long count = session.createQuery(
                            "select count(i.id) from Inscription i where i.etudiant.id = :etudiantId "
                                    + "and i.promotion.id = :promotionId "
                                    + "and (:excludedId is null or i.id <> :excludedId)", Long.class)
                    .setParameter("etudiantId", etudiantId)
                    .setParameter("promotionId", promotionId)
                    .setParameter("excludedId", excludedId)
                    .uniqueResult();
            return count != null && count > 0;
        }
    }
}
