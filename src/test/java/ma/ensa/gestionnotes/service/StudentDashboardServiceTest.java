package ma.ensa.gestionnotes.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

import ma.ensa.gestionnotes.model.Enseignement;
import ma.ensa.gestionnotes.model.Etudiant;
import ma.ensa.gestionnotes.model.Inscription;
import ma.ensa.gestionnotes.model.Module;
import ma.ensa.gestionnotes.model.Note;
import ma.ensa.gestionnotes.model.Promotion;

class StudentDashboardServiceTest {
    @Test
    void missingRequiredGradeDoesNotBecomeZeroOrProduceFinalAverage() {
        Etudiant etudiant = new Etudiant();
        Inscription inscription = inscription(1L, 10L);
        Enseignement first = teaching(10L, 20L, "A", 2);
        Enseignement second = teaching(10L, 21L, "B", 1);
        Note zeroGrade = new Note(0, Date.valueOf("2026-11-17"), inscription, first);

        StudentDashboardService.Dashboard dashboard = StudentDashboardService.calculer(etudiant,
                List.of(inscription), List.of(first, second), List.of(zeroGrade));

        assertFalse(dashboard.isMoyenneDisponible());
        assertNull(dashboard.getMoyenneGenerale());
        assertEquals("EN_ATTENTE", dashboard.getResultatFinal());
        assertEquals(1, dashboard.getModulesEvalues());
        assertEquals(2, dashboard.getModulesRequis());
        assertTrue(dashboard.getLignes().get(0).isEvalue());
        assertEquals(0.0, dashboard.getLignes().get(0).getValeur());
        assertFalse(dashboard.getLignes().get(1).isEvalue());
        assertNull(dashboard.getLignes().get(1).getValeur());
    }

    @Test
    void calculatesWeightedAverageOnlyWhenEveryRequiredGradeExists() {
        Etudiant etudiant = new Etudiant();
        Inscription inscription = inscription(1L, 10L);
        Enseignement first = teaching(10L, 20L, "A", 2);
        Enseignement second = teaching(10L, 21L, "B", 1);
        List<Note> notes = List.of(
                new Note(10, Date.valueOf("2026-11-16"), inscription, first),
                new Note(16, Date.valueOf("2026-11-17"), inscription, second));

        StudentDashboardService.Dashboard dashboard = StudentDashboardService.calculer(etudiant,
                List.of(inscription), List.of(first, second), notes);

        assertTrue(dashboard.isMoyenneDisponible());
        assertEquals(12.0, dashboard.getMoyenneGenerale(), 0.0001);
        assertEquals("ADMIS", dashboard.getResultatFinal());
        assertEquals(2, dashboard.getModulesEvalues());
    }

    @Test
    void notesForAnotherEnrollmentCannotCompleteTheStudentAverage() {
        Etudiant etudiant = new Etudiant();
        Inscription studentEnrollment = inscription(1L, 10L);
        Inscription otherEnrollment = inscription(2L, 10L);
        Enseignement teaching = teaching(10L, 20L, "A", 1);
        Note otherStudentGrade = new Note(18, Date.valueOf("2026-11-17"), otherEnrollment, teaching);

        StudentDashboardService.Dashboard dashboard = StudentDashboardService.calculer(etudiant,
                List.of(studentEnrollment), List.of(teaching), List.of(otherStudentGrade));

        assertFalse(dashboard.isMoyenneDisponible());
        assertEquals("EN_ATTENTE", dashboard.getResultatFinal());
        assertEquals(0, dashboard.getModulesEvalues());
    }

    @Test
    void completeAverageBelowTenIsAjourned() {
        Etudiant etudiant = new Etudiant();
        Inscription inscription = inscription(1L, 10L);
        Enseignement teaching = teaching(10L, 20L, "A", 1);
        Note grade = new Note(9.5, Date.valueOf("2026-11-17"), inscription, teaching);

        StudentDashboardService.Dashboard dashboard = StudentDashboardService.calculer(etudiant,
                List.of(inscription), List.of(teaching), List.of(grade));

        assertTrue(dashboard.isMoyenneDisponible());
        assertEquals(9.5, dashboard.getMoyenneGenerale(), 0.0001);
        assertEquals("AJOURNE", dashboard.getResultatFinal());
    }

    @Test
    void noRequiredTeachingHasNoAverageAndRemainsPending() {
        StudentDashboardService.Dashboard dashboard = StudentDashboardService.calculer(
                new Etudiant(), List.of(), List.of(), List.of());

        assertFalse(dashboard.isMoyenneDisponible());
        assertNull(dashboard.getMoyenneGenerale());
        assertEquals("EN_ATTENTE", dashboard.getResultatFinal());
        assertEquals(0, dashboard.getModulesRequis());
    }

    private Inscription inscription(long id, long promotionId) {
        Promotion promotion = new Promotion();
        promotion.setId(promotionId);
        Inscription inscription = new Inscription();
        inscription.setId(id);
        inscription.setPromotion(promotion);
        return inscription;
    }

    private Enseignement teaching(long promotionId, long moduleId, String code, double coefficient) {
        Promotion promotion = new Promotion();
        promotion.setId(promotionId);
        Module module = new Module(code, "Module " + code, coefficient);
        module.setId(moduleId);
        Enseignement teaching = new Enseignement();
        teaching.setId(moduleId + 100);
        teaching.setPromotion(promotion);
        teaching.setModule(module);
        return teaching;
    }
}
