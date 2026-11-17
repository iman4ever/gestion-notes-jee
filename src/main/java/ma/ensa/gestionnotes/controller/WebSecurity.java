package ma.ensa.gestionnotes.controller;

import java.security.SecureRandom;
import java.util.Base64;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ma.ensa.gestionnotes.model.Role;
import ma.ensa.gestionnotes.model.Utilisateur;

final class WebSecurity {
    private static final String CSRF_KEY = "csrfToken";
    private static final SecureRandom RANDOM = new SecureRandom();

    private WebSecurity() {
    }

    static Utilisateur requireRole(HttpServletRequest request, HttpServletResponse response, Role role)
            throws java.io.IOException {
        HttpSession session = request.getSession(false);
        Object value = session == null ? null : session.getAttribute("utilisateurConnecte");
        if (!(value instanceof Utilisateur utilisateur)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return null;
        }
        if (utilisateur.getRole() != role) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return null;
        }
        return utilisateur;
    }

    static String csrfToken(HttpSession session) {
        String token = (String) session.getAttribute(CSRF_KEY);
        if (token == null) {
            byte[] bytes = new byte[32];
            RANDOM.nextBytes(bytes);
            token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
            session.setAttribute(CSRF_KEY, token);
        }
        return token;
    }

    static boolean validCsrf(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        Object expected = session.getAttribute(CSRF_KEY);
        String actual = request.getParameter("csrfToken");
        return expected instanceof String token && actual != null && token.equals(actual);
    }
}
