package ma.ensa.gestionnotes.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ma.ensa.gestionnotes.model.Inscription;
import ma.ensa.gestionnotes.model.Note;
import ma.ensa.gestionnotes.model.Promotion;
import ma.ensa.gestionnotes.model.Role;
import ma.ensa.gestionnotes.service.InscriptionService;
import ma.ensa.gestionnotes.service.NoteService;
import ma.ensa.gestionnotes.service.PromotionService;

@WebServlet(name = "ResultatsPromotionServlet", urlPatterns = {"/admin/resultats"})
public class ResultatsPromotionServlet extends HttpServlet {
    private final PromotionService promotionService = new PromotionService();
    private final InscriptionService inscriptionService = new InscriptionService();
    private final NoteService noteService = new NoteService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (WebSecurity.requireRole(request, response, Role.ADMIN) == null) return;
        long promotionId;
        try {
            promotionId = Long.parseLong(request.getParameter("promotionId"));
            if (promotionId <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Promotion invalide.");
            return;
        }

        try {
            Promotion promotion = promotionService.trouverParId(promotionId);
            if (promotion == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Promotion introuvable.");
                return;
            }
            List<Inscription> inscriptions = inscriptionService.trouverTout().stream()
                    .filter(i -> i.getPromotion().getId().equals(promotionId)).toList();
            List<Note> notes = noteService.trouverTout();
            List<ResultatPromotion> resultats = new ArrayList<>();
            for (Inscription inscription : inscriptions) {
                double totalPondere = 0;
                double totalCoefficients = 0;
                int nombreNotes = 0;
                for (Note note : notes) {
                    if (note.getInscription().getId().equals(inscription.getId())
                            && note.getEnseignement().getPromotion().getId().equals(promotionId)) {
                        double coefficient = note.getEnseignement().getModule().getCoefficient();
                        if (coefficient > 0) {
                            totalPondere += note.getValeur() * coefficient;
                            totalCoefficients += coefficient;
                            nombreNotes++;
                        }
                    }
                }
                double moyenne = totalCoefficients == 0 ? 0 : totalPondere / totalCoefficients;
                String statut = totalCoefficients == 0 ? "EN_ATTENTE" : moyenne >= 10 ? "ADMIS" : "AJOURNE";
                resultats.add(new ResultatPromotion(inscription.getEtudiant().getIdEtudiant(),
                        inscription.getEtudiant().getNom(), inscription.getEtudiant().getPrenom(), nombreNotes,
                        moyenne, statut));
            }
            request.setAttribute("promotion", promotion);
            request.setAttribute("resultats", resultats);
            request.getRequestDispatcher("/WEB-INF/views/admin/resultats.jsp").forward(request, response);
        } catch (Exception e) {
            getServletContext().log("Impossible de calculer les résultats de la promotion " + promotionId, e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Impossible de calculer les résultats de cette promotion.");
        }
    }
}
