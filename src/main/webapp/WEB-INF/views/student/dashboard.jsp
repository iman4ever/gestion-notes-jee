<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="fr_FR"/>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Tableau de bord — Étudiant</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=20261117-student-dashboard">
</head>
<body class="student-dashboard-page">
<div class="container">
    <header class="topbar">
        <div>
            <h1>Tableau de bord — Étudiant</h1>
            <p>Bienvenue, <strong><c:out value="${utilisateurConnecte.nomUtilisateur}"/></strong></p>
        </div>
        <a class="btn danger" href="${pageContext.request.contextPath}/logout">Déconnexion</a>
    </header>

    <main class="student-dashboard">
        <section class="card student-profile">
            <div class="section-heading">
                <div>
                    <h2>Mon dossier académique</h2>
                    <p>Vos informations d'inscription et de parcours.</p>
                </div>
            </div>
            <dl class="profile-grid">
                <div><dt>Nom complet</dt><dd><c:out value="${dashboard.etudiant.prenom} ${dashboard.etudiant.nom}"/></dd></div>
                <div><dt>CNE</dt><dd><c:out value="${dashboard.etudiant.idEtudiant}"/></dd></div>
                <div>
                    <dt>Filière</dt>
                    <dd><c:out value="${dashboard.inscriptionPrincipale.promotion.filiere.libelle}" default="Non renseignée"/></dd>
                </div>
                <div>
                    <dt>Niveau</dt>
                    <dd><c:out value="${dashboard.inscriptionPrincipale.promotion.niveau.libelle}" default="Non renseigné"/></dd>
                </div>
                <div>
                    <dt>Promotion</dt>
                    <dd><c:out value="${dashboard.inscriptionPrincipale.promotion.libelle}" default="Non renseignée"/></dd>
                </div>
                <div>
                    <dt>Année universitaire</dt>
                    <dd><c:out value="${dashboard.inscriptionPrincipale.promotion.anneeUniversitaire.libelle}" default="Non renseignée"/></dd>
                </div>
            </dl>
        </section>

        <section class="card summary-card">
            <p class="summary-label">Moyenne générale</p>
            <p class="summary-value">
                <c:choose>
                    <c:when test="${dashboard.moyenneDisponible}">
                        <fmt:formatNumber value="${dashboard.moyenneGenerale}" minFractionDigits="2" maxFractionDigits="2"/> <span>/ 20</span>
                    </c:when>
                    <c:otherwise>Non disponible</c:otherwise>
                </c:choose>
            </p>
            <p class="summary-help">
                <c:choose>
                    <c:when test="${dashboard.moyenneDisponible}">Moyenne pondérée de vos modules.</c:when>
                    <c:otherwise>Elle sera calculée lorsque toutes les notes requises seront saisies.</c:otherwise>
                </c:choose>
            </p>
        </section>

        <section class="card summary-card">
            <p class="summary-label">Résultat final</p>
            <p class="summary-value">
                <c:choose>
                    <c:when test="${dashboard.resultatFinal == 'ADMIS'}">
                        <span class="status-badge status-admis">Admis</span>
                    </c:when>
                    <c:when test="${dashboard.resultatFinal == 'AJOURNE'}">
                        <span class="status-badge status-ajourne">Ajourné</span>
                    </c:when>
                    <c:otherwise>
                        <span class="status-badge status-attente">En attente</span>
                    </c:otherwise>
                </c:choose>
            </p>
            <p class="summary-help">Résultat établi à partir de toutes les notes requises.</p>
        </section>

        <section class="card summary-card">
            <p class="summary-label">Modules évalués</p>
            <p class="summary-value"><c:out value="${dashboard.modulesEvalues}"/> <span>/ <c:out value="${dashboard.modulesRequis}"/></span></p>
            <p class="summary-help">Modules avec une note saisie.</p>
        </section>

        <section class="card grades-card">
            <div class="section-heading">
                <div>
                    <h2>Mes notes</h2>
                    <p>Notes et évaluations de vos inscriptions.</p>
                </div>
            </div>
            <c:choose>
                <c:when test="${empty dashboard.lignes}">
                    <div class="student-empty-state">
                        <h3>Aucun module à afficher</h3>
                        <p>
                            <c:choose>
                                <c:when test="${empty dashboard.inscriptionPrincipale}">
                                    Aucune inscription académique n'est associée à votre dossier pour le moment.
                                </c:when>
                                <c:otherwise>
                                    Aucun enseignement n'est encore associé à votre promotion.
                                </c:otherwise>
                            </c:choose>
                        </p>
                    </div>
                </c:when>
                <c:otherwise>
                    <c:if test="${dashboard.modulesEvalues < dashboard.modulesRequis}">
                        <p class="grades-notice">Certaines notes sont encore en attente. Les modules concernés sont indiqués « Non saisie ».</p>
                    </c:if>
                    <div class="table-wrap">
                        <table>
                            <thead>
                            <tr><th>Module</th><th>Coefficient</th><th>Note /20</th><th>Date de saisie</th><th>Statut</th></tr>
                            </thead>
                            <tbody>
                            <c:forEach var="ligne" items="${dashboard.lignes}">
                                <tr>
                                    <td><c:out value="${ligne.module}"/></td>
                                    <td><fmt:formatNumber value="${ligne.coefficient}" minFractionDigits="0" maxFractionDigits="2"/></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${ligne.evalue}">
                                                <fmt:formatNumber value="${ligne.valeur}" minFractionDigits="2" maxFractionDigits="2"/>
                                            </c:when>
                                            <c:otherwise>Non saisie</c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${ligne.evalue && not empty ligne.dateSaisie}">
                                                <fmt:formatDate value="${ligne.dateSaisie}" pattern="dd/MM/yyyy"/>
                                            </c:when>
                                            <c:otherwise>—</c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${ligne.evalue}"><span class="grade-status grade-entered">Saisie</span></c:when>
                                            <c:otherwise><span class="grade-status grade-missing">Non saisie</span></c:otherwise>
                                        </c:choose>
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
