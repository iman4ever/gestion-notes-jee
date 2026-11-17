package ma.ensa.gestionnotes.dao;

import java.util.List;

import org.hibernate.Session;

import ma.ensa.gestionnotes.config.HibernateUtil;
import ma.ensa.gestionnotes.model.Professeur;

public class ProfesseurDao extends GenericDao<Professeur> {
    public ProfesseurDao() {
        super(Professeur.class);
    }

    public Professeur findByUtilisateurId(Long utilisateurId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "select p from Professeur p join fetch p.utilisateur where p.utilisateur.id = :id",
                            Professeur.class)
                    .setParameter("id", utilisateurId).uniqueResult();
        }
    }

    @Override
    public Professeur findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("select p from Professeur p join fetch p.utilisateur where p.id = :id",
                    Professeur.class).setParameter("id", id).uniqueResult();
        }
    }

    @Override
    public List<Professeur> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select p from Professeur p join fetch p.utilisateur order by p.nom, p.prenom",
                    Professeur.class).list();
        }
    }
}
