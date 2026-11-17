package ma.ensa.gestionnotes.controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import ma.ensa.gestionnotes.model.Role;
import ma.ensa.gestionnotes.model.Utilisateur;
import ma.ensa.gestionnotes.service.StudentDashboardService;

@WebServlet(name = "StudentDashboardServlet", urlPatterns = {"/student"})
public class StudentDashboardServlet extends HttpServlet {

    private final StudentDashboardService dashboardService = new StudentDashboardService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Utilisateur utilisateurConnecte = WebSecurity.requireRole(request, response, Role.ETUDIANT);
        if (utilisateurConnecte == null) return;

        try {
            StudentDashboardService.Dashboard dashboard = dashboardService.charger(utilisateurConnecte.getId());
            request.setAttribute("dashboard", dashboard);
            request.setAttribute("utilisateurConnecte", utilisateurConnecte);
        } catch (Exception e) {
            getServletContext().log("Impossible de charger les résultats de l'étudiant.", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossible de charger vos résultats.");
            return;
        }
        request.getRequestDispatcher("/WEB-INF/views/student/dashboard.jsp").forward(request, response);
    }
}
