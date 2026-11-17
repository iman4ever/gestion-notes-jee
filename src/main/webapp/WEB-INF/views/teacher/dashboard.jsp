<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="fr_FR"/>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Tableau de bord — Professeur</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=20261117-teacher-dashboard">
    <script src="${pageContext.request.contextPath}/js/teacher-dashboard.js?v=20261117-teacher-dashboard" defer></script>
</head>
<body class="teacher-dashboard-page">
<div class="container">
    <header class="topbar">
        <div>
            <h1>Tableau de bord — Professeur</h1>
            <p>Bienvenue, <strong><c:out value="${utilisateurConnecte.nomUtilisateur}"/></strong></p>
        </div>
        <a class="btn danger" href="${pageContext.request.contextPath}/logout">Déconnexion</a>
    </header>

    <c:if test="${not empty param.message}"><div class="alert success" role="status"><c:out value="${param.message}"/></div></c:if>
    <c:if test="${not empty param.error}"><div class="alert error" role="alert"><c:out value="${param.error}"/></div></c:if>

    <main class="teacher-dashboard">
        <section class="card teacher-summary-card">
            <p class="teacher-summary-label">Enseignements attribués</p>
            <p class="teacher-summary-value"><c:out value="${enseignements.size()}"/></p>
        </section>
        <section class="card teacher-summary-card">
            <p class="teacher-summary-label">Promotions suivies</p>
            <p class="teacher-summary-value"><c:out value="${nombrePromotions}"/></p>
        </section>
        <section class="card teacher-summary-card">
            <p class="teacher-summary-label">Notes saisies</p>
            <p class="teacher-summary-value"><c:out value="${notes.size()}"/></p>
        </section>

        <section class="card teacher-wide">
            <div class="teacher-section-heading">
                <div>
                    <h2>Mes enseignements</h2>
                    <p>Enseignements et promotions qui vous sont attribués.</p>
                </div>
            </div>
            <c:choose>
                <c:when test="${empty enseignementsAvecEffectifs}">
                    <div class="teacher-empty-state">
                        Aucun enseignement ne vous a encore été attribué. Veuillez contacter l'administration.
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="table-wrap">
                        <table class="teacher-wide-table">
                            <thead>
                            <tr>
                                <th>Module</th><th>Coefficient</th><th>Promotion</th><th>Filière</th>
                                <th>Niveau</th><th>Année universitaire</th><th>Professeur</th><th>Étudiants inscrits</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="ligne" items="${enseignementsAvecEffectifs}">
                                <tr>
                                    <td><strong><c:out value="${ligne.enseignement.module.code}"/></strong> — <c:out value="${ligne.enseignement.module.libelle}"/></td>
                                    <td><fmt:formatNumber value="${ligne.enseignement.module.coefficient}" minFractionDigits="0" maxFractionDigits="2"/></td>
                                    <td><c:out value="${ligne.enseignement.promotion.libelle}"/></td>
                                    <td><c:out value="${ligne.enseignement.promotion.filiere.libelle}"/></td>
                                    <td><c:out value="${ligne.enseignement.promotion.niveau.libelle}"/></td>
                                    <td><c:out value="${ligne.enseignement.promotion.anneeUniversitaire.libelle}"/></td>
                                    <td><c:out value="${ligne.enseignement.professeur.prenom} ${ligne.enseignement.professeur.nom}"/></td>
                                    <td><c:out value="${ligne.nombreEtudiants}"/></td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>
        </section>

        <section class="card teacher-form-card">
            <div class="teacher-section-heading">
                <div>
                    <h2><c:choose><c:when test="${not empty editingNote}">Modifier une note</c:when><c:otherwise>Saisir une note</c:otherwise></c:choose></h2>
                    <p>Choisissez un enseignement et un étudiant inscrit à sa promotion.</p>
                </div>
            </div>
            <form id="teacher-grade-form" action="${pageContext.request.contextPath}/teacher/notes" method="post">
                <input type="hidden" name="csrfToken" value="${csrfToken}">
                <c:if test="${not empty editingNote}"><input type="hidden" name="id" value="<c:out value='${editingNote.id}'/>"></c:if>

                <div class="field">
                    <label for="enseignementId">Enseignement</label>
                    <select id="enseignementId" name="enseignementId" required>
                        <option value="">-- Sélectionner un enseignement --</option>
                        <c:forEach var="e" items="${enseignements}">
                            <option value="<c:out value='${e.id}'/>"
                                    data-promotion-id="<c:out value='${e.promotion.id}'/>"
                                    ${not empty editingNote && editingNote.enseignement.id == e.id ? 'selected' : ''}>
                                <c:out value="${e.module.code} — ${e.module.libelle} — ${e.promotion.libelle}"/>
                            </option>
                        </c:forEach>
                    </select>
                    <c:if test="${empty enseignements}">
                        <span class="teacher-field-help">Aucun enseignement ne vous a encore été attribué.</span>
                    </c:if>
                </div>

                <div class="field">
                    <label for="inscriptionId">Étudiant inscrit</label>
                    <select id="inscriptionId" name="inscriptionId" required
                            data-selected-enrollment="${not empty editingNote ? editingNote.inscription.id : ''}">
                        <option value="">Sélectionnez d'abord un enseignement</option>
                        <c:forEach var="i" items="${inscriptions}">
                            <option value="<c:out value='${i.id}'/>"
                                    data-promotion-id="<c:out value='${i.promotion.id}'/>"
                                    ${not empty editingNote && editingNote.inscription.id == i.id ? 'selected' : ''}>
                                <c:out value="${i.etudiant.idEtudiant} — ${i.etudiant.prenom} ${i.etudiant.nom}"/>
                            </option>
                        </c:forEach>
                    </select>
                    <span id="teacher-student-help" class="teacher-field-help" aria-live="polite">
                        <c:if test="${empty inscriptions}">Aucun étudiant n'est inscrit aux promotions de vos enseignements.</c:if>
                    </span>
                </div>

                <div class="field">
                    <label for="valeur">Note sur 20</label>
                    <input id="valeur" type="number" name="valeur" min="0" max="20" step="0.01"
                           value="<c:out value='${editingNote.valeur}'/>" required>
                </div>

                <div class="field">
                    <label for="dateSaisie">Date de saisie</label>
                    <input id="dateSaisie" type="date" name="dateSaisie"
                           value="<c:out value='${dateSaisieInitiale}'/>" required>
                </div>

                <div class="teacher-form-actions">
                    <button type="submit">Enregistrer la note</button>
                    <c:if test="${not empty editingNote}">
                        <a class="btn secondary" href="${pageContext.request.contextPath}/teacher">Annuler</a>
                    </c:if>
                </div>
            </form>
        </section>

        <section class="card teacher-wide">
            <div class="teacher-section-heading">
                <div>
                    <h2>Notes de mes enseignements</h2>
                    <p>Notes que vous avez saisies pour vos étudiants.</p>
                </div>
            </div>
            <c:choose>
                <c:when test="${empty notes}">
                    <div class="teacher-empty-state">Aucune note n'a encore été saisie pour vos enseignements.</div>
                </c:when>
                <c:otherwise>
                    <div class="table-wrap">
                        <table class="teacher-wide-table">
                            <thead>
                            <tr><th>Étudiant</th><th>CNE</th><th>Module</th><th>Promotion</th><th>Note /20</th><th>Date de saisie</th><th>Actions</th></tr>
                            </thead>
                            <tbody>
                            <c:forEach var="n" items="${notes}">
                                <tr>
                                    <td><c:out value="${n.inscription.etudiant.prenom} ${n.inscription.etudiant.nom}"/></td>
                                    <td><c:out value="${n.inscription.etudiant.idEtudiant}"/></td>
                                    <td><c:out value="${n.enseignement.module.code} — ${n.enseignement.module.libelle}"/></td>
                                    <td><c:out value="${n.enseignement.promotion.libelle}"/></td>
                                    <td><span class="teacher-grade-badge"><fmt:formatNumber value="${n.valeur}" minFractionDigits="2" maxFractionDigits="2"/></span></td>
                                    <td><fmt:formatDate value="${n.dateSaisie}" pattern="dd/MM/yyyy"/></td>
                                    <td class="actions">
                                        <a class="btn secondary" href="${pageContext.request.contextPath}/teacher?editNoteId=<c:out value='${n.id}'/>">Modifier</a>
                                        <form class="inline-form" action="${pageContext.request.contextPath}/teacher/notes" method="post"
                                              onsubmit="return confirm('Supprimer cette note ?');">
                                            <input type="hidden" name="csrfToken" value="${csrfToken}">
                                            <input type="hidden" name="id" value="<c:out value='${n.id}'/>">
                                            <input type="hidden" name="operation" value="delete">
                                            <button class="btn danger" type="submit">Supprimer</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>
        </section>
    </main>
</div>
</body>
</html>
