package ma.ensa.gestionnotes.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import ma.ensa.gestionnotes.model.Role;
import ma.ensa.gestionnotes.model.Utilisateur;
import ma.ensa.gestionnotes.service.UtilisateurService;
import ma.ensa.gestionnotes.util.SeedDataService;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    private final UtilisateurService utilisateurService = new UtilisateurService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        new SeedDataService().ensureDefaultData();
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            new SeedDataService().ensureDefaultData();
            Utilisateur utilisateur = utilisateurService.authentifier(username, password);

            if (utilisateur == null) {
                request.setAttribute("error", "Identifiants incorrects. Veuillez réessayer.");
                request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
                return;
            }

            HttpSession session = request.getSession();
            session.setAttribute("utilisateurConnecte", utilisateur);

            if (utilisateur.getRole() == Role.ADMIN) {
                response.sendRedirect(request.getContextPath() + "/admin");
            } else if (utilisateur.getRole() == Role.PROFESSEUR) {
                response.sendRedirect(request.getContextPath() + "/teacher");
            } else {
                response.sendRedirect(request.getContextPath() + "/student");
            }
        } catch (Exception e) {
            request.setAttribute("error", "Connexion impossible : " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        }
    }
}
