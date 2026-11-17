package ma.ensa.gestionnotes.dao;

import java.util.List;

import org.hibernate.Session;

import ma.ensa.gestionnotes.config.HibernateUtil;
import ma.ensa.gestionnotes.model.Role;
import ma.ensa.gestionnotes.model.Utilisateur;

public class UtilisateurDao extends GenericDao<Utilisateur> {
    public UtilisateurDao() {
        super(Utilisateur.class);
    }

    public Utilisateur findByUsername(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Utilisateur u where u.nomUtilisateur = :username", Utilisateur.class)
                    .setParameter("username", username).uniqueResult();
        }
    }

    public List<Utilisateur> findByRole(Role role) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Utilisateur u where u.role = :role", Utilisateur.class)
                    .setParameter("role", role).list();
        }
    }

    public boolean existsByUsername(String username, Long excludedId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long count = session.createQuery(
                            "select count(u.id) from Utilisateur u where lower(u.nomUtilisateur) = lower(:username) "
                                    + "and (:excludedId is null or u.id <> :excludedId)", Long.class)
                    .setParameter("username", username).setParameter("excludedId", excludedId).uniqueResult();
            return count != null && count > 0;
        }
    }

    public boolean existsByEmail(String email, Long excludedId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long count = session.createQuery(
                            "select count(u.id) from Utilisateur u where lower(u.email) = lower(:email) "
                                    + "and (:excludedId is null or u.id <> :excludedId)", Long.class)
                    .setParameter("email", email).setParameter("excludedId", excludedId).uniqueResult();
            return count != null && count > 0;
        }
    }
}
