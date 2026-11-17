package ma.ensa.gestionnotes.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ma.ensa.gestionnotes.model.AnneeUniversitaire;
import ma.ensa.gestionnotes.model.Enseignement;
import ma.ensa.gestionnotes.model.Etudiant;
import ma.ensa.gestionnotes.model.Inscription;
import ma.ensa.gestionnotes.model.Module;
import ma.ensa.gestionnotes.model.Note;
import ma.ensa.gestionnotes.model.Promotion;

class BusinessValidationTest {
    @Test
    void studentCreationRequiresCneAndAccountBeforeDatabaseAccess() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> new EtudiantService().enregistrer(new Etudiant()));

        assertTrue(error.getMessage().contains("étudiant"));
    }

    @Test
    void promotionRequiresItsThreeUmlReferences() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> new PromotionService().enregistrer(new Promotion()));

        assertTrue(error.getMessage().contains("filière"));
    }

    @Test
    void moduleCoefficientMustBePositive() {
        Module module = new Module("M-1", "Module test", 0);

        assertThrows(IllegalArgumentException.class, () -> new ModuleService().enregistrer(module));
    }

    @Test
    void academicYearEndMustNotPrecedeItsStart() {
        AnneeUniversitaire year = new AnneeUniversitaire("2025-2026",
                java.sql.Date.valueOf("2026-09-01"), java.sql.Date.valueOf("2026-08-31"));

        assertThrows(IllegalArgumentException.class, () -> new AnneeUniversitaireService().enregistrer(year));
    }

    @Test
    void gradeMustBeBetweenZeroAndTwenty() {
        Inscription enrollment = new Inscription();
        enrollment.setId(1L);
        Enseignement teaching = new Enseignement();
        teaching.setId(1L);
        Note note = new Note(20.1, java.sql.Date.valueOf("2026-11-13"), enrollment, teaching);

        assertThrows(IllegalArgumentException.class, () -> new NoteService().enregistrer(note));
    }
}
