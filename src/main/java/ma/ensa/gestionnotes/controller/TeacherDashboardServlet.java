package ma.ensa.gestionnotes.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import ma.ensa.gestionnotes.model.Enseignement;
import ma.ensa.gestionnotes.model.Inscription;
import ma.ensa.gestionnotes.model.Note;
import ma.ensa.gestionnotes.model.Professeur;
import ma.ensa.gestionnotes.model.Role;
import ma.ensa.gestionnotes.model.Utilisateur;
import ma.ensa.gestionnotes.service.EnseignementService;
import ma.ensa.gestionnotes.service.InscriptionService;
import ma.ensa.gestionnotes.service.NoteService;
import ma.ensa.gestionnotes.service.ProfesseurService;

@WebServlet(name = "TeacherDashboardServlet", urlPatterns = { "/teacher" })
public class TeacherDashboardServlet extends HttpServlet {

    private final ProfesseurService professeurService = new ProfesseurService();
    private final EnseignementService enseignementService = new EnseignementService();
    private final InscriptionService inscriptionService = new InscriptionService();
    private final NoteService noteService = new NoteService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Utilisateur utilisateurConnecte = WebSecurity.requireRole(request, response, Role.PROFESSEUR);
        if (utilisateurConnecte == null) return;

        try {
            Long utilisateurId = utilisateurConnecte.getId();
            Professeur professeur = professeurService.trouverParUtilisateurId(utilisateurId);
            if (professeur == null) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "Aucun profil professeur n'est associé à ce compte.");
                return;
            }

            List<Enseignement> enseignements = enseignementService.trouverPourProfesseur(utilisateurId);
            List<Long> promotionIds = enseignements.stream()
                    .map(enseignement -> enseignement.getPromotion().getId())
                    .distinct()
                    .toList();
            List<Inscription> inscriptions = inscriptionService.trouverPourPromotions(promotionIds);
            List<Note> notes = noteService.trouverPourProfesseur(utilisateurConnecte.getId());

            Map<Long, Integer> studentCounts = new HashMap<>();
            for (Inscription inscription : inscriptions) {
                studentCounts.merge(inscription.getPromotion().getId(), 1, Integer::sum);
            }
            List<TeacherTeachingRow> enseignementsAvecEffectifs = enseignements.stream()
                    .map(enseignement -> new TeacherTeachingRow(enseignement,
                            studentCounts.getOrDefault(enseignement.getPromotion().getId(), 0)))
                    .toList();
            long nombrePromotions = enseignements.stream()
                    .map(enseignement -> enseignement.getPromotion().getId())
                    .distinct()
                    .count();

            request.setAttribute("enseignements", enseignements);
            request.setAttribute("enseignementsAvecEffectifs", enseignementsAvecEffectifs);
            request.setAttribute("notes", notes);
            request.setAttribute("inscriptions", inscriptions);
            request.setAttribute("nombrePromotions", nombrePromotions);
            request.setAttribute("dateSaisieInitiale", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
            request.setAttribute("professeur", professeur);
            request.setAttribute("utilisateurConnecte", utilisateurConnecte);
            request.setAttribute("csrfToken", WebSecurity.csrfToken(request.getSession()));

            String editId = request.getParameter("editNoteId");
            if (editId != null && !editId.isBlank()) {
                long id = Long.parseLong(editId);
                if (!noteService.appartientAuProfesseur(id, utilisateurId)) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
                Note editingNote = noteService.trouverParId(id);
                if (editingNote == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Note introuvable.");
                    return;
                }
                request.setAttribute("editingNote", editingNote);
                request.setAttribute("dateSaisieInitiale",
                        new java.text.SimpleDateFormat("yyyy-MM-dd").format(editingNote.getDateSaisie()));
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Identifiant de note invalide.");
            return;
        } catch (Exception e) {
            getServletContext().log("Impossible de charger le tableau de bord professeur.", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Impossible de charger les données du professeur.");
            return;
        }

        request.getRequestDispatcher("/WEB-INF/views/teacher/dashboard.jsp").forward(request, response);
    }
}
