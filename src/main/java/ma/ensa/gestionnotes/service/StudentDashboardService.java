package ma.ensa.gestionnotes.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ma.ensa.gestionnotes.model.Enseignement;
import ma.ensa.gestionnotes.model.Etudiant;
import ma.ensa.gestionnotes.model.Inscription;
import ma.ensa.gestionnotes.model.Note;

public class StudentDashboardService {
    private final EtudiantService etudiantService = new EtudiantService();
    private final InscriptionService inscriptionService = new InscriptionService();
    private final EnseignementService enseignementService = new EnseignementService();
    private final NoteService noteService = new NoteService();

    public Dashboard charger(Long utilisateurId) {
        Etudiant etudiant = etudiantService.trouverParUtilisateurId(utilisateurId);
        if (etudiant == null) {
            throw new IllegalStateException("Aucun profil étudiant n'est associé à ce compte.");
        }

        List<Inscription> inscriptions = inscriptionService.trouverPourEtudiant(utilisateurId);
        List<Long> promotionIds = inscriptions.stream()
                .map(inscription -> inscription.getPromotion().getId())
                .distinct()
                .toList();
        List<Enseignement> enseignements = enseignementService.trouverPourPromotions(promotionIds);
        List<Note> notes = noteService.trouverPourEtudiant(utilisateurId);
        return calculer(etudiant, inscriptions, enseignements, notes);
    }

    static Dashboard calculer(Etudiant etudiant, List<Inscription> inscriptions,
            List<Enseignement> enseignements, List<Note> notes) {
        Map<Long, List<Enseignement>> enseignementsParPromotion = new HashMap<>();
        for (Enseignement enseignement : enseignements) {
            enseignementsParPromotion.computeIfAbsent(enseignement.getPromotion().getId(), key -> new ArrayList<>())
                    .add(enseignement);
        }

        Set<GradeKey> affectationsRequises = new HashSet<>();
        for (Inscription inscription : inscriptions) {
            List<Enseignement> promotionEnseignements =
                    enseignementsParPromotion.getOrDefault(inscription.getPromotion().getId(), List.of());
            for (Enseignement enseignement : promotionEnseignements) {
                affectationsRequises.add(new GradeKey(inscription.getId(), enseignement.getId()));
            }
        }

        Map<GradeKey, Note> notesParAffectation = new HashMap<>();
        for (Note note : notes) {
            GradeKey key = new GradeKey(note.getInscription().getId(), note.getEnseignement().getId());
            if (affectationsRequises.contains(key)) {
                notesParAffectation.putIfAbsent(key, note);
            }
        }

        List<GradeRow> lignes = new ArrayList<>();
        double totalPondere = 0;
        double totalCoefficients = 0;
        int modulesEvalues = 0;
        for (Inscription inscription : inscriptions) {
            List<Enseignement> promotionEnseignements =
                    enseignementsParPromotion.getOrDefault(inscription.getPromotion().getId(), List.of());
            for (Enseignement enseignement : promotionEnseignements) {
                double coefficient = enseignement.getModule().getCoefficient();
                if (!Double.isFinite(coefficient) || coefficient <= 0) {
                    throw new IllegalStateException("Un module requis possède un coefficient invalide.");
                }

                Note note = notesParAffectation.get(new GradeKey(inscription.getId(), enseignement.getId()));
                if (note != null) {
                    if (!Double.isFinite(note.getValeur()) || note.getValeur() < 0 || note.getValeur() > 20) {
                        throw new IllegalStateException("Une note enregistrée est en dehors de l'intervalle autorisé.");
                    }
                    totalPondere += note.getValeur() * coefficient;
                    totalCoefficients += coefficient;
                    modulesEvalues++;
                }
                lignes.add(new GradeRow(enseignement.getModule().getCode() + " — "
                        + enseignement.getModule().getLibelle(), coefficient, note));
            }
        }

        int modulesRequis = affectationsRequises.size();
        boolean moyenneDisponible = modulesRequis > 0 && modulesEvalues == modulesRequis;
        Double moyenne = moyenneDisponible ? totalPondere / totalCoefficients : null;
        String resultatFinal = !moyenneDisponible ? "EN_ATTENTE" : moyenne >= 10 ? "ADMIS" : "AJOURNE";
        Inscription inscriptionPrincipale = inscriptions.isEmpty() ? null : inscriptions.get(0);
        return new Dashboard(etudiant, inscriptionPrincipale, lignes, moyenne, modulesEvalues,
                modulesRequis, resultatFinal);
    }

    private record GradeKey(Long inscriptionId, Long enseignementId) {
    }

    public static final class Dashboard {
        private final Etudiant etudiant;
        private final Inscription inscriptionPrincipale;
        private final List<GradeRow> lignes;
        private final Double moyenneGenerale;
        private final int modulesEvalues;
        private final int modulesRequis;
        private final String resultatFinal;

        private Dashboard(Etudiant etudiant, Inscription inscriptionPrincipale, List<GradeRow> lignes,
                Double moyenneGenerale, int modulesEvalues, int modulesRequis, String resultatFinal) {
            this.etudiant = etudiant;
            this.inscriptionPrincipale = inscriptionPrincipale;
            this.lignes = List.copyOf(lignes);
            this.moyenneGenerale = moyenneGenerale;
            this.modulesEvalues = modulesEvalues;
            this.modulesRequis = modulesRequis;
            this.resultatFinal = resultatFinal;
        }

        public Etudiant getEtudiant() {
            return etudiant;
        }

        public Inscription getInscriptionPrincipale() {
            return inscriptionPrincipale;
        }

        public List<GradeRow> getLignes() {
            return lignes;
        }

        public Double getMoyenneGenerale() {
            return moyenneGenerale;
        }

        public boolean isMoyenneDisponible() {
            return moyenneGenerale != null;
        }

        public int getModulesEvalues() {
            return modulesEvalues;
        }

        public int getModulesRequis() {
            return modulesRequis;
        }

        public String getResultatFinal() {
            return resultatFinal;
        }
    }

    public static final class GradeRow {
        private final String module;
        private final double coefficient;
        private final Double valeur;
        private final java.util.Date dateSaisie;

        private GradeRow(String module, double coefficient, Note note) {
            this.module = module;
            this.coefficient = coefficient;
            this.valeur = note == null ? null : note.getValeur();
            this.dateSaisie = note == null ? null : note.getDateSaisie();
        }

        public String getModule() {
            return module;
        }

        public double getCoefficient() {
            return coefficient;
        }

        public Double getValeur() {
            return valeur;
        }

        public java.util.Date getDateSaisie() {
            return dateSaisie;
        }

        public boolean isEvalue() {
            return valeur != null;
        }
    }
}
