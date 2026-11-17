<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Administration — Gestion des notes</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=20261117-admin-ui">
</head>
<body class="admin-dashboard-page">
<div class="container">
    <header class="topbar">
        <div>
            <h1>Tableau de bord administrateur</h1>
            <p>Bienvenue, <strong><c:out value="${utilisateurConnecte.nomUtilisateur}"/></strong></p>
        </div>
        <div class="topbar-actions">
            <a class="btn danger" href="${pageContext.request.contextPath}/logout">Déconnexion</a>
        </div>
    </header>

    <c:if test="${not empty param.message}"><div class="alert success"><c:out value="${param.message}"/></div></c:if>
    <main class="dashboard-grid">
        <section class="card metric-card">
            <p class="metric-label">Étudiants inscrits</p>
            <p class="metric"><c:out value="${etudiants.size()}"/></p>
        </section>
        <section class="card metric-card">
            <p class="metric-label">Professeurs</p>
            <p class="metric"><c:out value="${professeurs.size()}"/></p>
        </section>
        <section class="card metric-card">
            <p class="metric-label">Filières</p>
            <p class="metric"><c:out value="${filieres.size()}"/></p>
        </section>
        <section class="card metric-card">
            <p class="metric-label">Promotions</p>
            <p class="metric"><c:out value="${promotions.size()}"/></p>
        </section>

        <section class="card wide">
            <h2>Gestion des données académiques</h2>
            <nav class="management-links" aria-label="Gestion des données académiques">
                <a class="management-card" href="${pageContext.request.contextPath}/admin/etudiants">
                    <strong>Étudiants</strong><span>Profils, comptes et informations personnelles</span>
                </a>
                <a class="management-card" href="${pageContext.request.contextPath}/admin/gestion?type=professeurs">
                    <strong>Professeurs</strong><span>Comptes et spécialités</span>
                </a>
                <a class="management-card" href="${pageContext.request.contextPath}/admin/gestion?type=filieres">
                    <strong>Filières</strong><span>Parcours et formations</span>
                </a>
                <a class="management-card" href="${pageContext.request.contextPath}/admin/gestion?type=niveaux">
                    <strong>Niveaux</strong><span>Niveaux d'études</span>
                </a>
                <a class="management-card" href="${pageContext.request.contextPath}/admin/gestion?type=annees">
                    <strong>Années universitaires</strong><span>Calendrier académique</span>
                </a>
                <a class="management-card" href="${pageContext.request.contextPath}/admin/gestion?type=promotions">
                    <strong>Promotions</strong><span>Groupes et résultats</span>
                </a>
                <a class="management-card" href="${pageContext.request.contextPath}/admin/gestion?type=modules">
                    <strong>Modules</strong><span>Matières et coefficients</span>
                </a>
                <a class="management-card" href="${pageContext.request.contextPath}/admin/gestion?type=inscriptions">
                    <strong>Inscriptions</strong><span>Étudiants et promotions</span>
                </a>
                <a class="management-card" href="${pageContext.request.contextPath}/admin/gestion?type=enseignements">
                    <strong>Enseignements / affectations</strong><span>Professeurs, modules et promotions</span>
                </a>
            </nav>
        </section>
    </main>
</div>
</body>
</html>
