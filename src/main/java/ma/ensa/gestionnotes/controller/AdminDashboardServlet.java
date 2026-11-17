package ma.ensa.gestionnotes.controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import ma.ensa.gestionnotes.model.Role;
import ma.ensa.gestionnotes.model.Utilisateur;
import ma.ensa.gestionnotes.service.EtudiantService;
import ma.ensa.gestionnotes.service.FiliereService;
import ma.ensa.gestionnotes.service.ProfesseurService;
import ma.ensa.gestionnotes.service.PromotionService;
import ma.ensa.gestionnotes.service.UtilisateurService;

@WebServlet(name = "AdminDashboardServlet", urlPatterns = {"/admin"})
public class AdminDashboardServlet extends HttpServlet {

    private final UtilisateurService utilisateurService = new UtilisateurService();
    private final EtudiantService etudiantService = new EtudiantService();
    private final ProfesseurService professeurService = new ProfesseurService();
    private final FiliereService filiereService = new FiliereService();
    private final PromotionService promotionService = new PromotionService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Utilisateur utilisateurConnecte = WebSecurity.requireRole(request, response, Role.ADMIN);
        if (utilisateurConnecte == null) return;

        try {
            request.setAttribute("utilisateurs", utilisateurService.trouverTout());
            request.setAttribute("etudiants", etudiantService.trouverTout());
            request.setAttribute("professeurs", professeurService.trouverTout());
            request.setAttribute("filieres", filiereService.trouverTout());
            request.setAttribute("promotions", promotionService.trouverTout());
        } catch (Exception e) {
            getServletContext().log("Impossible de charger le tableau de bord administrateur.", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Impossible de charger les données administratives.");
            return;
        }

        request.setAttribute("utilisateurConnecte", utilisateurConnecte);

        request.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(request, response);
    }
}
