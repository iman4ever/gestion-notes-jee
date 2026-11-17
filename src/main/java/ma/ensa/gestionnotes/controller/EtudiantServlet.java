package ma.ensa.gestionnotes.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ma.ensa.gestionnotes.model.Etudiant;
import ma.ensa.gestionnotes.model.Role;
import ma.ensa.gestionnotes.model.Utilisateur;
import ma.ensa.gestionnotes.service.EtudiantService;

@WebServlet(name = "EtudiantServlet", urlPatterns = {"/admin/etudiants"})
public class EtudiantServlet extends HttpServlet {
    private final EtudiantService etudiantService = new EtudiantService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (WebSecurity.requireRole(request, response, Role.ADMIN) == null) return;
        request.setCharacterEncoding("UTF-8");
        Long id = parseOptionalId(request.getParameter("id"));
        Etudiant editing = id == null ? null : etudiantService.trouverParId(id);
        if (id != null && editing == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        request.setAttribute("editing", editing != null);
        request.setAttribute("etudiant", editing);
        request.setAttribute("etudiants", etudiantService.trouverTout());
        request.setAttribute("csrfToken", WebSecurity.csrfToken(request.getSession()));
        request.getRequestDispatcher("/WEB-INF/views/admin/etudiants.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (WebSecurity.requireRole(request, response, Role.ADMIN) == null) return;
        request.setCharacterEncoding("UTF-8");
        if (!WebSecurity.validCsrf(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        try {
            if ("delete".equals(request.getParameter("operation"))) {
                Etudiant etudiant = etudiantService.trouverParId(requiredId(request.getParameter("id")));
                if (etudiant == null) throw new IllegalArgumentException("Étudiant introuvable.");
                etudiantService.supprimer(etudiant);
                redirect(response, request, "Étudiant supprimé.");
                return;
            }

            Long id = parseOptionalId(request.getParameter("id"));
            Etudiant etudiant = id == null ? new Etudiant() : etudiantService.trouverParId(id);
            if (etudiant == null) throw new IllegalArgumentException("Étudiant introuvable.");

            etudiant.setIdEtudiant(required(request, "cne"));
            etudiant.setNom(required(request, "nom"));
            etudiant.setPrenom(required(request, "prenom"));
            etudiant.setSexe(required(request, "sexe"));
            String dateNaissance = request.getParameter("dateNaissance");
            etudiant.setDateNaissance(dateNaissance == null || dateNaissance.isBlank() ? null : parseDate(dateNaissance));

            Utilisateur utilisateur = etudiant.getUtilisateur();
            if (utilisateur == null) utilisateur = new Utilisateur();
            utilisateur.setNomUtilisateur(required(request, "nomUtilisateur"));
            utilisateur.setEmail(required(request, "email"));
            utilisateur.setRole(Role.ETUDIANT);
            if (id == null) utilisateur.setActif(true);
            String password = request.getParameter("motDePasse");
            if (id == null) {
                utilisateur.setMotDePasse(required(request, "motDePasse"));
            } else if (password != null && !password.isBlank()) {
                utilisateur.setMotDePasse(password);
            } else if (utilisateur.getMotDePasse() == null || utilisateur.getMotDePasse().isBlank()) {
                throw new IllegalArgumentException("Le mot de passe est obligatoire pour un nouvel étudiant.");
            }
            etudiant.setUtilisateur(utilisateur);

            if (id == null) etudiantService.enregistrer(etudiant);
            else etudiantService.modifier(etudiant);
            redirect(response, request, "Étudiant enregistré avec succès.");
        } catch (IllegalArgumentException e) {
            renderError(request, response, e.getMessage());
        } catch (Exception e) {
            getServletContext().log("Échec d'une opération CRUD étudiant", e);
            renderError(request, response,
                    "L'opération a échoué. Vérifiez le CNE, l'email et les références en base.");
        }
    }

    private void renderError(HttpServletRequest request, HttpServletResponse response, String message)
            throws ServletException, IOException {
        Long id = parseOptionalId(request.getParameter("id"));
        Etudiant existing = id == null ? null : etudiantService.trouverParId(id);
        request.setAttribute("editing", id != null);
        request.setAttribute("etudiant", existing);
        request.setAttribute("etudiants", etudiantService.trouverTout());
        request.setAttribute("error", message);
        request.setAttribute("csrfToken", WebSecurity.csrfToken(request.getSession()));
        request.setAttribute("formCne", parameterOr(request, "cne", existing == null ? "" : existing.getIdEtudiant()));
        request.setAttribute("formNom", parameterOr(request, "nom", existing == null ? "" : existing.getNom()));
        request.setAttribute("formPrenom", parameterOr(request, "prenom", existing == null ? "" : existing.getPrenom()));
        request.setAttribute("formSexe", parameterOr(request, "sexe", existing == null ? "" : existing.getSexe()));
        request.setAttribute("formDate", parameterOr(request, "dateNaissance",
                existing == null ? "" : date(existing.getDateNaissance())));
        request.setAttribute("formUsername", parameterOr(request, "nomUtilisateur",
                existing == null || existing.getUtilisateur() == null ? "" : existing.getUtilisateur().getNomUtilisateur()));
        request.setAttribute("formEmail", parameterOr(request, "email",
                existing == null || existing.getUtilisateur() == null ? "" : existing.getUtilisateur().getEmail()));
        request.getRequestDispatcher("/WEB-INF/views/admin/etudiants.jsp").forward(request, response);
    }

    private String parameterOr(HttpServletRequest request, String name, String fallback) {
        String value = request.getParameter(name);
        return value == null ? fallback : value;
    }

    private void redirect(HttpServletResponse response, HttpServletRequest request, String message) throws IOException {
        response.sendRedirect(request.getContextPath() + "/admin/etudiants?message="
                + java.net.URLEncoder.encode(message, java.nio.charset.StandardCharsets.UTF_8));
    }

    private String required(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Le champ « " + name + " » est obligatoire.");
        }
        return value.trim();
    }

    private Long requiredId(String value) {
        Long id = parseOptionalId(value);
        if (id == null) throw new IllegalArgumentException("Identifiant invalide.");
        return id;
    }

    private Long parseOptionalId(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            long id = Long.parseLong(value);
            if (id <= 0) throw new NumberFormatException();
            return id;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Identifiant invalide.");
        }
    }

    private Date parseDate(String value) {
        try {
            return java.sql.Date.valueOf(LocalDate.parse(value));
        } catch (java.time.DateTimeException e) {
            throw new IllegalArgumentException("La date de naissance n'est pas valide.");
        }
    }

    private String date(Date value) {
        return value == null ? "" : new java.text.SimpleDateFormat("yyyy-MM-dd").format(value);
    }
}
