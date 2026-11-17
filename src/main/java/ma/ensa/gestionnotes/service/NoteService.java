package ma.ensa.gestionnotes.service;

import java.util.List;

import ma.ensa.gestionnotes.dao.NoteDao;
import ma.ensa.gestionnotes.model.Note;

public class NoteService {

    private final NoteDao noteDao = new NoteDao();

    public Note enregistrer(Note note) {
        valider(note);
        return noteDao.save(note);
    }

    public Note modifier(Note note) {
        valider(note);
        return noteDao.update(note);
    }

    public Note enregistrerPourProfesseur(Note note, Long utilisateurId) {
        validerPourProfesseur(note, utilisateurId, null);
        return noteDao.save(note);
    }

    public Note modifierPourProfesseur(Note note, Long utilisateurId) {
        if (note == null || note.getId() == null || !noteDao.belongsToProfessor(note.getId(), utilisateurId)) {
            throw new SecurityException("Vous ne pouvez modifier que les notes de vos enseignements.");
        }
        validerPourProfesseur(note, utilisateurId, note.getId());
        return noteDao.update(note);
    }

    public void supprimer(Note note) {
        noteDao.delete(note);
    }

    public void supprimerPourProfesseur(Long noteId, Long utilisateurId) {
        if (noteId == null || !noteDao.belongsToProfessor(noteId, utilisateurId)) {
            throw new SecurityException("Vous ne pouvez supprimer que les notes de vos enseignements.");
        }
        Note note = noteDao.findById(noteId);
        if (note == null) {
            throw new IllegalArgumentException("Note introuvable.");
        }
        noteDao.delete(note);
    }

    public Note trouverParId(Long id) {
        return noteDao.findById(id);
    }

    public List<Note> trouverTout() {
        return noteDao.findAll();
    }

    public List<Note> trouverPourEtudiant(Long utilisateurId) {
        return noteDao.findByEtudiantUtilisateurId(utilisateurId);
    }

    public List<Note> trouverPourProfesseur(Long utilisateurId) {
        return noteDao.findByProfesseurUtilisateurId(utilisateurId);
    }

    public boolean appartientAuProfesseur(Long noteId, Long utilisateurId) {
        return noteDao.belongsToProfessor(noteId, utilisateurId);
    }

    public boolean enseignementAppartientAuProfesseur(Long enseignementId, Long utilisateurId) {
        return noteDao.teachingBelongsToProfessor(enseignementId, utilisateurId);
    }

    public boolean inscriptionCorrespondAEnseignement(Long inscriptionId, Long enseignementId) {
        return noteDao.enrollmentMatchesTeaching(inscriptionId, enseignementId);
    }

    private void validerPourProfesseur(Note note, Long utilisateurId, Long excludedNoteId) {
        valider(note);
        if (!noteDao.teachingBelongsToProfessor(note.getEnseignement().getId(), utilisateurId)) {
            throw new SecurityException("Vous ne pouvez saisir des notes que pour vos enseignements.");
        }
        if (!noteDao.enrollmentMatchesTeaching(note.getInscription().getId(), note.getEnseignement().getId())) {
            throw new IllegalArgumentException("L'étudiant n'est pas inscrit à la promotion de cet enseignement.");
        }
        if (noteDao.existsForEnrollmentAndTeaching(note.getInscription().getId(),
                note.getEnseignement().getId(), excludedNoteId)) {
            throw new IllegalArgumentException("Une note existe déjà pour cet étudiant et cet enseignement. Modifiez la note existante.");
        }
    }

    private void valider(Note note) {
        if (note == null || note.getDateSaisie() == null || note.getInscription() == null
                || note.getInscription().getId() == null || note.getEnseignement() == null
                || note.getEnseignement().getId() == null) {
            throw new IllegalArgumentException("La note, sa date, l'inscription et l'enseignement sont obligatoires.");
        }
        if (!Double.isFinite(note.getValeur()) || note.getValeur() < 0 || note.getValeur() > 20) {
            throw new IllegalArgumentException("La note doit être comprise entre 0 et 20.");
        }
    }
}
