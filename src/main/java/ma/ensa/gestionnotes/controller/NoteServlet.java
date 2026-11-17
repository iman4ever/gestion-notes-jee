package ma.ensa.gestionnotes.controller;

import java.io.IOException;
import java.time.LocalDate;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ma.ensa.gestionnotes.model.Enseignement;
import ma.ensa.gestionnotes.model.Inscription;
import ma.ensa.gestionnotes.model.Note;
import ma.ensa.gestionnotes.model.Role;
import ma.ensa.gestionnotes.model.Utilisateur;
import ma.ensa.gestionnotes.service.EnseignementService;
import ma.ensa.gestionnotes.service.InscriptionService;
import ma.ensa.gestionnotes.service.NoteService;

@WebServlet(name = "NoteServlet", urlPatterns = {"/teacher/notes"})
public class NoteServlet extends HttpServlet {
    private final NoteService noteService = new NoteService();
    private final EnseignementService enseignementService = new EnseignementService();
    private final InscriptionService inscriptionService = new InscriptionService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Utilisateur teacher = WebSecurity.requireRole(request, response, Role.PROFESSEUR);
        if (teacher == null) return;
        request.setCharacterEncoding("UTF-8");
        if (!WebSecurity.validCsrf(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        try {
            Long id = optionalId(request.getParameter("id"));
            if ("delete".equals(request.getParameter("operation"))) {
                if (id == null || !noteService.appartientAuProfesseur(id, teacher.getId())) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
                noteService.supprimerPourProfesseur(id, teacher.getId());
                redirect(response, request, "Note supprimée.", false);
                return;
            }

            Long teachingId = requiredId(request.getParameter("enseignementId"));
            Long enrollmentId = requiredId(request.getParameter("inscriptionId"));
            if (!noteService.enseignementAppartientAuProfesseur(teachingId, teacher.getId())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            if (!noteService.inscriptionCorrespondAEnseignement(enrollmentId, teachingId)) {
                throw new IllegalArgumentException("L'étudiant n'est pas inscrit à la promotion de cet enseignement.");
            }
            Note note = id == null ? new Note() : noteService.trouverParId(id);
            if (note == null) {
                throw new IllegalArgumentException("Note introuvable.");
            }
            if (id != null && !noteService.appartientAuProfesseur(id, teacher.getId())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            note.setValeur(parseGrade(request.getParameter("valeur")));
            note.setDateSaisie(java.sql.Date.valueOf(LocalDate.parse(required(request, "dateSaisie"))));
            note.setEnseignement(requireFound(enseignementService.trouverParId(teachingId)));
            note.setInscription(requireFound(inscriptionService.trouverParId(enrollmentId)));
            if (id == null) noteService.enregistrerPourProfesseur(note, teacher.getId());
            else noteService.modifierPourProfesseur(note, teacher.getId());
            redirect(response, request, "Note enregistrée.", false);
        } catch (SecurityException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        } catch (IllegalArgumentException | java.time.DateTimeException e) {
            redirect(response, request, e.getMessage(), true);
        } catch (Exception e) {
            getServletContext().log("Échec d'une opération de note par un professeur", e);
            redirect(response, request, "Impossible d'enregistrer la note. Vérifiez les données saisies.", true);
        }
    }

    private String required(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        if (value == null || value.isBlank()) throw new IllegalArgumentException("Tous les champs sont obligatoires.");
        return value.trim();
    }

    private Long requiredId(String value) {
        Long id = optionalId(value);
        if (id == null) throw new IllegalArgumentException("L'enseignement et l'inscription sont obligatoires.");
        return id;
    }

    private Long optionalId(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            long id = Long.parseLong(value);
            if (id <= 0) throw new NumberFormatException();
            return id;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Identifiant invalide.");
        }
    }

    private double parseGrade(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("La note doit être un nombre entre 0 et 20.");
        }
    }

    private <T> T requireFound(T value) {
        if (value == null) throw new IllegalArgumentException("Référence introuvable.");
        return value;
    }

    private void redirect(HttpServletResponse response, HttpServletRequest request, String message, boolean error)
            throws IOException {
        String parameter = error ? "error" : "message";
        response.sendRedirect(request.getContextPath() + "/teacher?" + parameter + "="
                + java.net.URLEncoder.encode(message, java.nio.charset.StandardCharsets.UTF_8));
    }
}
