package ma.ensa.gestionnotes.dao;

import java.util.List;

import org.hibernate.Session;

import ma.ensa.gestionnotes.config.HibernateUtil;
import ma.ensa.gestionnotes.model.Note;

public class NoteDao extends GenericDao<Note> {
    public NoteDao() {
        super(Note.class);
    }

    @Override
    public List<Note> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select n from Note n join fetch n.inscription i join fetch i.etudiant et "
                            + "join fetch et.utilisateur join fetch i.promotion "
                            + "join fetch n.enseignement en join fetch en.module join fetch en.promotion "
                            + "join fetch en.professeur order by n.dateSaisie desc",
                    Note.class).list();
        }
    }

    public List<Note> findByEtudiantUtilisateurId(Long utilisateurId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select n from Note n join fetch n.inscription i join fetch i.etudiant et "
                            + "join fetch et.utilisateur u join fetch n.enseignement en join fetch en.module "
                            + "join fetch en.promotion "
                            + "where u.id = :id order by n.dateSaisie desc",
                    Note.class).setParameter("id", utilisateurId).list();
        }
    }

    public List<Note> findByProfesseurUtilisateurId(Long utilisateurId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select n from Note n join fetch n.inscription i join fetch i.etudiant et "
                            + "join fetch i.promotion ip join fetch ip.filiere join fetch ip.niveau "
                            + "join fetch ip.anneeUniversitaire join fetch n.enseignement en "
                            + "join fetch en.promotion ep join fetch ep.filiere join fetch ep.niveau "
                            + "join fetch ep.anneeUniversitaire join fetch en.module "
                            + "join fetch en.professeur p join fetch p.utilisateur u "
                            + "where u.id = :id order by n.dateSaisie desc",
                    Note.class).setParameter("id", utilisateurId).list();
        }
    }

    @Override
    public Note findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select n from Note n join fetch n.inscription i join fetch i.etudiant e "
                            + "join fetch e.utilisateur join fetch i.promotion ip "
                            + "join fetch ip.filiere join fetch ip.niveau join fetch ip.anneeUniversitaire "
                            + "join fetch n.enseignement en join fetch en.promotion ep "
                            + "join fetch ep.filiere join fetch ep.niveau join fetch ep.anneeUniversitaire "
                            + "join fetch en.module join fetch en.professeur p join fetch p.utilisateur "
                            + "where n.id = :id",
                    Note.class).setParameter("id", id).uniqueResult();
        }
    }

    public boolean belongsToProfessor(Long noteId, Long utilisateurId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long count = session.createQuery(
                            "select count(n.id) from Note n where n.id = :noteId "
                                    + "and n.enseignement.professeur.utilisateur.id = :userId", Long.class)
                    .setParameter("noteId", noteId).setParameter("userId", utilisateurId).uniqueResult();
            return count != null && count > 0;
        }
    }

    public boolean teachingBelongsToProfessor(Long teachingId, Long utilisateurId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long count = session.createQuery(
                            "select count(e.id) from Enseignement e "
                                    + "where e.id = :teachingId and e.professeur.utilisateur.id = :userId", Long.class)
                    .setParameter("teachingId", teachingId).setParameter("userId", utilisateurId).uniqueResult();
            return count != null && count > 0;
        }
    }

    public boolean enrollmentMatchesTeaching(Long enrollmentId, Long teachingId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long count = session.createQuery(
                            "select count(i.id) from Inscription i, Enseignement e "
                                    + "where i.id = :enrollmentId and e.id = :teachingId "
                                    + "and i.promotion.id = e.promotion.id", Long.class)
                    .setParameter("enrollmentId", enrollmentId).setParameter("teachingId", teachingId).uniqueResult();
            return count != null && count > 0;
        }
    }

    public boolean existsForEnrollmentAndTeaching(Long enrollmentId, Long teachingId, Long excludedNoteId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long count = session.createQuery(
                            "select count(n.id) from Note n where n.inscription.id = :enrollmentId "
                                    + "and n.enseignement.id = :teachingId "
                                    + "and (:excludedId is null or n.id <> :excludedId)", Long.class)
                    .setParameter("enrollmentId", enrollmentId)
                    .setParameter("teachingId", teachingId)
                    .setParameter("excludedId", excludedNoteId)
                    .uniqueResult();
            return count != null && count > 0;
        }
    }
}
