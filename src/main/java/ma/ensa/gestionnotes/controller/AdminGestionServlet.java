package ma.ensa.gestionnotes.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ma.ensa.gestionnotes.model.AnneeUniversitaire;
import ma.ensa.gestionnotes.model.Enseignement;
import ma.ensa.gestionnotes.model.Etudiant;
import ma.ensa.gestionnotes.model.Filiere;
import ma.ensa.gestionnotes.model.Inscription;
import ma.ensa.gestionnotes.model.Module;
import ma.ensa.gestionnotes.model.Niveau;
import ma.ensa.gestionnotes.model.Professeur;
import ma.ensa.gestionnotes.model.Promotion;
import ma.ensa.gestionnotes.model.Role;
import ma.ensa.gestionnotes.model.Utilisateur;
import ma.ensa.gestionnotes.service.AnneeUniversitaireService;
import ma.ensa.gestionnotes.service.EnseignementService;
import ma.ensa.gestionnotes.service.EtudiantService;
import ma.ensa.gestionnotes.service.FiliereService;
import ma.ensa.gestionnotes.service.InscriptionService;
import ma.ensa.gestionnotes.service.ModuleService;
import ma.ensa.gestionnotes.service.NiveauService;
import ma.ensa.gestionnotes.service.ProfesseurService;
import ma.ensa.gestionnotes.service.PromotionService;

@WebServlet(name = "AdminGestionServlet", urlPatterns = {"/admin/gestion"})
public class AdminGestionServlet extends HttpServlet {
    private final FiliereService filiereService = new FiliereService();
    private final NiveauService niveauService = new NiveauService();
    private final AnneeUniversitaireService anneeService = new AnneeUniversitaireService();
    private final PromotionService promotionService = new PromotionService();
    private final ModuleService moduleService = new ModuleService();
    private final EtudiantService etudiantService = new EtudiantService();
    private final ProfesseurService professeurService = new ProfesseurService();
    private final InscriptionService inscriptionService = new InscriptionService();
    private final EnseignementService enseignementService = new EnseignementService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (WebSecurity.requireRole(request, response, ma.ensa.gestionnotes.model.Role.ADMIN) == null) {
            return;
        }
        request.setCharacterEncoding("UTF-8");
        String type = request.getParameter("type");
        if (!supported(type)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        Long editId = parseOptionalId(request.getParameter("id"));
        Object editing = editId == null ? null : find(type, editId);
        if ("edit".equals(request.getParameter("action")) && editing == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        preparePage(request, type, editing);
        HttpSession session = request.getSession();
        request.setAttribute("csrfToken", WebSecurity.csrfToken(session));
        request.getRequestDispatcher("/WEB-INF/views/admin/gestion.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (WebSecurity.requireRole(request, response, ma.ensa.gestionnotes.model.Role.ADMIN) == null) {
            return;
        }
        request.setCharacterEncoding("UTF-8");
        if (!WebSecurity.validCsrf(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        String type = request.getParameter("type");
        if (!supported(type)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        try {
            if ("delete".equals(request.getParameter("operation"))) {
                delete(type, requiredId(request.getParameter("id")));
                redirect(response, request, type, "Enregistrement supprimé.");
                return;
            }
            Object entity = bind(request, type);
            boolean editing = entityId(entity) != null;
            save(type, entity, editing);
            redirect(response, request, type, editing ? "Modifications enregistrées." : "Enregistrement créé.");
        } catch (IllegalArgumentException e) {
            renderError(request, response, type, e.getMessage());
        } catch (Exception e) {
            getServletContext().log("Échec d'une opération d'administration pour " + type, e);
            renderError(request, response, type,
                    "L'opération a échoué. Vérifiez les données et les liens utilisés par cet enregistrement.");
        }
    }

    private void preparePage(HttpServletRequest request, String type, Object editing) {
        List<String> columnLabels = headers(type);
        List<AdminRow> records = rows(type);
        request.setAttribute("type", type);
        request.setAttribute("title", title(type));
        request.setAttribute("adminColumnLabels", columnLabels);
        request.setAttribute("adminRecordRows", records);
        request.setAttribute("adminColumnCount", columnLabels.size() + 2);
        request.setAttribute("fields", fields(type, editing));
        request.setAttribute("editingId", editing == null ? "" : entityId(editing));
        request.setAttribute("editing", editing != null);
    }

    private void renderError(HttpServletRequest request, HttpServletResponse response, String type, String message)
            throws ServletException, IOException {
        Object editing = null;
        Long id = parseOptionalId(request.getParameter("id"));
        if (id != null) {
            editing = find(type, id);
        }
        preparePage(request, type, editing);
        request.setAttribute("error", message);
        request.setAttribute("csrfToken", WebSecurity.csrfToken(request.getSession()));
        @SuppressWarnings("unchecked")
        List<AdminField> currentFields = (List<AdminField>) request.getAttribute("fields");
        for (AdminField field : currentFields) {
            String posted = request.getParameter(field.getName());
            if (posted != null) {
                fieldValuesOverride(request, field, posted);
            }
        }
        request.getRequestDispatcher("/WEB-INF/views/admin/gestion.jsp").forward(request, response);
    }

    private void fieldValuesOverride(HttpServletRequest request, AdminField field, String posted) {
        @SuppressWarnings("unchecked")
        List<AdminField> fields = (List<AdminField>) request.getAttribute("fields");
        int index = fields.indexOf(field);
        fields.set(index, new AdminField(field.getName(), field.getLabel(), field.getType(), posted,
                field.isRequired(), field.getOptions()));
    }

    private void redirect(HttpServletResponse response, HttpServletRequest request, String type, String message)
            throws IOException {
        response.sendRedirect(request.getContextPath() + "/admin/gestion?type=" + type + "&message="
                + java.net.URLEncoder.encode(message, java.nio.charset.StandardCharsets.UTF_8));
    }

    private boolean supported(String type) {
        return List.of("professeurs", "filieres", "niveaux", "annees", "promotions", "modules", "inscriptions",
                "enseignements")
                .contains(type);
    }

    private String title(String type) {
        return switch (type) {
            case "professeurs" -> "Professeurs";
            case "filieres" -> "Filières";
            case "niveaux" -> "Niveaux";
            case "annees" -> "Années universitaires";
            case "promotions" -> "Promotions";
            case "modules" -> "Modules";
            case "inscriptions" -> "Inscriptions";
            case "enseignements" -> "Enseignements";
            default -> "";
        };
    }

    private List<String> headers(String type) {
        return switch (type) {
            case "professeurs" -> List.of("Matricule", "Nom", "Prénom", "Spécialité", "Nom d'utilisateur", "Email");
            case "filieres", "niveaux" -> List.of("Code", "Libellé");
            case "modules" -> List.of("Code", "Libellé", "Coefficient");
            case "promotions" -> List.of("Code", "Libellé", "Filière / niveau / année universitaire");
            case "annees" -> List.of("Libellé", "Début", "Fin");
            case "inscriptions" -> List.of("Étudiant", "Promotion", "Date d'inscription");
            case "enseignements" -> List.of("Promotion", "Module", "Professeur");
            default -> List.of();
        };
    }

    private List<AdminRow> rows(String type) {
        List<AdminRow> rows = new ArrayList<>();
        switch (type) {
            case "professeurs" -> professeurService.trouverTout().forEach(x -> rows.add(new AdminRow(x.getId(),
                    List.of(x.getIdProfesseur(), x.getNom(), x.getPrenom(), x.getSpecialite(),
                            x.getUtilisateur().getNomUtilisateur(), x.getUtilisateur().getEmail()))));
            case "filieres" -> filiereService.trouverTout().forEach(x ->
                    rows.add(new AdminRow(x.getId(), List.of(x.getCode(), x.getLibelle()))));
            case "niveaux" -> niveauService.trouverTout().forEach(x ->
                    rows.add(new AdminRow(x.getId(), List.of(x.getCode(), x.getLibelle()))));
            case "annees" -> anneeService.trouverTout().forEach(x ->
                    rows.add(new AdminRow(x.getId(), List.of(x.getLibelle(), date(x.getDateDebut()), date(x.getDateFin())))));
            case "promotions" -> promotionService.trouverTout().forEach(x -> rows.add(new AdminRow(x.getId(),
                    List.of(x.getCode(), x.getLibelle(), label(x.getFiliere()) + " / " + label(x.getNiveau())
                            + " / " + label(x.getAnneeUniversitaire())))));
            case "modules" -> moduleService.trouverTout().forEach(x ->
                    rows.add(new AdminRow(x.getId(), List.of(x.getCode(), x.getLibelle(), Double.toString(x.getCoefficient())))));
            case "inscriptions" -> inscriptionService.trouverTout().forEach(x -> rows.add(new AdminRow(x.getId(),
                    List.of(fullName(x.getEtudiant()), label(x.getPromotion()), date(x.getDateInscription())))));
            case "enseignements" -> enseignementService.trouverTout().forEach(x -> rows.add(new AdminRow(x.getId(),
                    List.of(label(x.getPromotion()), label(x.getModule()), fullName(x.getProfesseur())))));
            default -> throw new IllegalArgumentException("Type de données inconnu.");
        }
        return rows;
    }

    private List<AdminField> fields(String type, Object entity) {
        List<AdminField> fields = new ArrayList<>();
        switch (type) {
            case "professeurs" -> {
                Professeur x = (Professeur) entity;
                add(fields, "idProfesseur", "Matricule", "text", x == null ? "" : x.getIdProfesseur(), true);
                add(fields, "nom", "Nom", "text", x == null ? "" : x.getNom(), true);
                add(fields, "prenom", "Prénom", "text", x == null ? "" : x.getPrenom(), true);
                add(fields, "specialite", "Spécialité", "text", x == null ? "" : x.getSpecialite(), true);
                add(fields, "nomUtilisateur", "Nom d'utilisateur", "text",
                        x == null ? "" : x.getUtilisateur().getNomUtilisateur(), true);
                add(fields, "email", "Email", "email", x == null ? "" : x.getUtilisateur().getEmail(), true);
                add(fields, "motDePasse", "Mot de passe (vide = inchangé)", "password", "", x == null);
            }
            case "filieres" -> {
                Filiere x = (Filiere) entity;
                add(fields, "code", "Code", "text", x == null ? "" : x.getCode(), true);
                add(fields, "libelle", "Libellé", "text", x == null ? "" : x.getLibelle(), true);
            }
            case "niveaux" -> {
                Niveau x = (Niveau) entity;
                add(fields, "code", "Code", "text", x == null ? "" : x.getCode(), true);
                add(fields, "libelle", "Libellé", "text", x == null ? "" : x.getLibelle(), true);
            }
            case "annees" -> {
                AnneeUniversitaire x = (AnneeUniversitaire) entity;
                add(fields, "libelle", "Libellé", "text", x == null ? "" : x.getLibelle(), true);
                add(fields, "dateDebut", "Date de début", "date", x == null ? "" : date(x.getDateDebut()), true);
                add(fields, "dateFin", "Date de fin", "date", x == null ? "" : date(x.getDateFin()), true);
            }
            case "promotions" -> {
                Promotion x = (Promotion) entity;
                add(fields, "code", "Code", "text", x == null ? "" : x.getCode(), true);
                add(fields, "libelle", "Libellé", "text", x == null ? "" : x.getLibelle(), true);
                select(fields, "filiereId", "Filière", x == null ? null : x.getFiliere(),
                        filiereService.trouverTout(), "libelle");
                select(fields, "niveauId", "Niveau", x == null ? null : x.getNiveau(),
                        niveauService.trouverTout(), "libelle");
                select(fields, "anneeId", "Année universitaire", x == null ? null : x.getAnneeUniversitaire(),
                        anneeService.trouverTout(), "libelle");
            }
            case "modules" -> {
                Module x = (Module) entity;
                add(fields, "code", "Code", "text", x == null ? "" : x.getCode(), true);
                add(fields, "libelle", "Libellé", "text", x == null ? "" : x.getLibelle(), true);
                add(fields, "coefficient", "Coefficient", "number", x == null ? "" : Double.toString(x.getCoefficient()), true);
            }
            case "inscriptions" -> {
                Inscription x = (Inscription) entity;
                add(fields, "dateInscription", "Date d'inscription", "date",
                        x == null ? "" : date(x.getDateInscription()), true);
                select(fields, "etudiantId", "Étudiant", x == null ? null : x.getEtudiant(),
                        etudiantService.trouverTout(), "fullName");
                select(fields, "promotionId", "Promotion", x == null ? null : x.getPromotion(),
                        promotionService.trouverTout(), "label");
            }
            case "enseignements" -> {
                Enseignement x = (Enseignement) entity;
                select(fields, "promotionId", "Promotion", x == null ? null : x.getPromotion(),
                        promotionService.trouverTout(), "label");
                select(fields, "moduleId", "Module", x == null ? null : x.getModule(),
                        moduleService.trouverTout(), "label");
                select(fields, "professeurId", "Professeur", x == null ? null : x.getProfesseur(),
                        professeurService.trouverTout(), "fullName");
            }
            default -> throw new IllegalArgumentException("Type de données inconnu.");
        }
        if (entity != null) {
            fields.add(new AdminField("id", "Identifiant", "hidden", entityId(entity).toString(), false, List.of()));
        }
        return fields;
    }

    private void add(List<AdminField> fields, String name, String label, String type, String value, boolean required) {
        fields.add(new AdminField(name, label, type, value, required, List.of()));
    }

    private void select(List<AdminField> fields, String name, String label, Object selected, List<?> values,
            String labelType) {
        List<AdminOption> options = new ArrayList<>();
        for (Object value : values) {
            options.add(new AdminOption(entityId(value).toString(),
                    switch (labelType) {
                        case "fullName" -> fullName(value);
                        case "label" -> label(value);
                        default -> label(value);
                    }));
        }
        add(fields, name, label, "select", selected == null ? "" : entityId(selected).toString(), true);
        int index = fields.size() - 1;
        fields.set(index, new AdminField(name, label, "select", selected == null ? "" : entityId(selected).toString(),
                true, options));
    }

    private Object find(String type, Long id) {
        return switch (type) {
            case "professeurs" -> professeurService.trouverParId(id);
            case "filieres" -> filiereService.trouverParId(id);
            case "niveaux" -> niveauService.trouverParId(id);
            case "annees" -> anneeService.trouverParId(id);
            case "promotions" -> promotionService.trouverParId(id);
            case "modules" -> moduleService.trouverParId(id);
            case "inscriptions" -> inscriptionService.trouverParId(id);
            case "enseignements" -> enseignementService.trouverParId(id);
            default -> null;
        };
    }

    private Object bind(HttpServletRequest request, String type) {
        Long id = parseOptionalId(request.getParameter("id"));
        return switch (type) {
            case "professeurs" -> {
                Professeur x = id == null ? new Professeur() : (Professeur) find(type, id);
                requireFound(x);
                x.setIdProfesseur(required(request, "idProfesseur"));
                x.setNom(required(request, "nom"));
                x.setPrenom(required(request, "prenom"));
                x.setSpecialite(required(request, "specialite"));
                Utilisateur user = x.getUtilisateur() == null ? new Utilisateur() : x.getUtilisateur();
                user.setNomUtilisateur(required(request, "nomUtilisateur"));
                user.setEmail(required(request, "email"));
                user.setRole(Role.PROFESSEUR);
                if (id == null) user.setActif(true);
                String password = request.getParameter("motDePasse");
                if (id == null) {
                    if (password == null || password.isBlank()) {
                        throw new IllegalArgumentException("Le mot de passe est obligatoire pour un nouveau professeur.");
                    }
                    user.setMotDePasse(password);
                } else if (password != null && !password.isBlank()) {
                    user.setMotDePasse(password);
                }
                x.setUtilisateur(user);
                yield x;
            }
            case "filieres" -> {
                Filiere x = id == null ? new Filiere() : (Filiere) find(type, id);
                requireFound(x);
                x.setCode(required(request, "code"));
                x.setLibelle(required(request, "libelle"));
                yield x;
            }
            case "niveaux" -> {
                Niveau x = id == null ? new Niveau() : (Niveau) find(type, id);
                requireFound(x);
                x.setCode(required(request, "code"));
                x.setLibelle(required(request, "libelle"));
                yield x;
            }
            case "annees" -> {
                AnneeUniversitaire x = id == null ? new AnneeUniversitaire() : (AnneeUniversitaire) find(type, id);
                requireFound(x);
                x.setLibelle(required(request, "libelle"));
                x.setDateDebut(parseDate(required(request, "dateDebut")));
                x.setDateFin(parseDate(required(request, "dateFin")));
                if (x.getDateFin().before(x.getDateDebut())) {
                    throw new IllegalArgumentException("La date de fin doit être postérieure à la date de début.");
                }
                yield x;
            }
            case "promotions" -> {
                Promotion x = id == null ? new Promotion() : (Promotion) find(type, id);
                requireFound(x);
                x.setCode(required(request, "code"));
                x.setLibelle(required(request, "libelle"));
                x.setFiliere(requireFound(filiereService.trouverParId(requiredId(request.getParameter("filiereId")))));
                x.setNiveau(requireFound(niveauService.trouverParId(requiredId(request.getParameter("niveauId")))));
                x.setAnneeUniversitaire(requireFound(
                        anneeService.trouverParId(requiredId(request.getParameter("anneeId")))));
                yield x;
            }
            case "modules" -> {
                Module x = id == null ? new Module() : (Module) find(type, id);
                requireFound(x);
                x.setCode(required(request, "code"));
                x.setLibelle(required(request, "libelle"));
                double coefficient = parseDouble(required(request, "coefficient"), "coefficient");
                if (!Double.isFinite(coefficient) || coefficient <= 0) {
                    throw new IllegalArgumentException("Le coefficient doit être un nombre positif.");
                }
                x.setCoefficient(coefficient);
                yield x;
            }
            case "inscriptions" -> {
                Inscription x = id == null ? new Inscription() : (Inscription) find(type, id);
                requireFound(x);
                x.setDateInscription(parseDate(required(request, "dateInscription")));
                x.setEtudiant(requireFound(etudiantService.trouverParId(requiredId(request.getParameter("etudiantId")))));
                x.setPromotion(requireFound(promotionService.trouverParId(requiredId(request.getParameter("promotionId")))));
                yield x;
            }
            case "enseignements" -> {
                Enseignement x = id == null ? new Enseignement() : (Enseignement) find(type, id);
                requireFound(x);
                x.setPromotion(requireFound(promotionService.trouverParId(requiredId(request.getParameter("promotionId")))));
                x.setModule(requireFound(moduleService.trouverParId(requiredId(request.getParameter("moduleId")))));
                x.setProfesseur(requireFound(professeurService.trouverParId(requiredId(request.getParameter("professeurId")))));
                yield x;
            }
            default -> throw new IllegalArgumentException("Type de données inconnu.");
        };
    }

    private void save(String type, Object entity, boolean editing) {
        switch (type) {
            case "professeurs" -> {
                if (editing) professeurService.modifier((Professeur) entity);
                else professeurService.enregistrer((Professeur) entity);
            }
            case "filieres" -> {
                if (editing) filiereService.modifier((Filiere) entity); else filiereService.enregistrer((Filiere) entity);
            }
            case "niveaux" -> {
                if (editing) niveauService.modifier((Niveau) entity); else niveauService.enregistrer((Niveau) entity);
            }
            case "annees" -> {
                if (editing) anneeService.modifier((AnneeUniversitaire) entity);
                else anneeService.enregistrer((AnneeUniversitaire) entity);
            }
            case "promotions" -> {
                if (editing) promotionService.modifier((Promotion) entity);
                else promotionService.enregistrer((Promotion) entity);
            }
            case "modules" -> {
                if (editing) moduleService.modifier((Module) entity); else moduleService.enregistrer((Module) entity);
            }
            case "inscriptions" -> {
                if (editing) inscriptionService.modifier((Inscription) entity);
                else inscriptionService.enregistrer((Inscription) entity);
            }
            case "enseignements" -> {
                if (editing) enseignementService.modifier((Enseignement) entity);
                else enseignementService.enregistrer((Enseignement) entity);
            }
            default -> throw new IllegalArgumentException("Type de données inconnu.");
        }
    }

    private void delete(String type, Long id) {
        Object entity = requireFound(find(type, id));
        switch (type) {
            case "professeurs" -> professeurService.supprimer((Professeur) entity);
            case "filieres" -> filiereService.supprimer((Filiere) entity);
            case "niveaux" -> niveauService.supprimer((Niveau) entity);
            case "annees" -> anneeService.supprimer((AnneeUniversitaire) entity);
            case "promotions" -> promotionService.supprimer((Promotion) entity);
            case "modules" -> moduleService.supprimer((Module) entity);
            case "inscriptions" -> inscriptionService.supprimer((Inscription) entity);
            case "enseignements" -> enseignementService.supprimer((Enseignement) entity);
            default -> throw new IllegalArgumentException("Type de données inconnu.");
        }
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
        if (id == null) {
            throw new IllegalArgumentException("Une référence obligatoire est manquante ou invalide.");
        }
        return id;
    }

    private Long parseOptionalId(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            long id = Long.parseLong(value);
            if (id <= 0) throw new NumberFormatException();
            return id;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Identifiant invalide.");
        }
    }

    private double parseDouble(String value, String label) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Le champ « " + label + " » doit être numérique.");
        }
    }

    private Date parseDate(String value) {
        try {
            return java.sql.Date.valueOf(LocalDate.parse(value));
        } catch (java.time.DateTimeException e) {
            throw new IllegalArgumentException("La date renseignée n'est pas valide.");
        }
    }

    private String date(Date value) {
        return value == null ? "" : new java.text.SimpleDateFormat("yyyy-MM-dd").format(value);
    }

    private String label(Object value) {
        if (value == null) return "—";
        if (value instanceof Filiere x) return x.getLibelle();
        if (value instanceof Niveau x) return x.getLibelle();
        if (value instanceof AnneeUniversitaire x) return x.getLibelle();
        if (value instanceof Promotion x) return x.getLibelle();
        if (value instanceof Module x) return x.getLibelle();
        return String.valueOf(value);
    }

    private String fullName(Object value) {
        if (value instanceof Etudiant x) return x.getIdEtudiant() + " — " + x.getNom() + " " + x.getPrenom();
        if (value instanceof Professeur x) return x.getIdProfesseur() + " — " + x.getNom() + " " + x.getPrenom();
        return label(value);
    }

    private Long entityId(Object value) {
        if (value instanceof Filiere x) return x.getId();
        if (value instanceof Niveau x) return x.getId();
        if (value instanceof AnneeUniversitaire x) return x.getId();
        if (value instanceof Promotion x) return x.getId();
        if (value instanceof Module x) return x.getId();
        if (value instanceof Etudiant x) return x.getId();
        if (value instanceof Professeur x) return x.getId();
        if (value instanceof Inscription x) return x.getId();
        if (value instanceof Enseignement x) return x.getId();
        throw new IllegalArgumentException("Type de données inconnu.");
    }

    private <T> T requireFound(T value) {
        if (value == null) {
            throw new IllegalArgumentException("Enregistrement introuvable.");
        }
        return value;
    }
}
