<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Résultats de promotion</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="container">
    <header class="topbar">
        <h1>Résultats — <c:out value="${promotion.libelle}"/></h1>
        <nav><a class="btn" href="${pageContext.request.contextPath}/admin/gestion?type=promotions">Promotions</a>
            <a class="btn" href="${pageContext.request.contextPath}/admin">Tableau de bord</a></nav>
    </header>
    <section class="card wide">
        <div class="table-wrap">
            <table>
                <thead><tr><th>CNE</th><th>Nom</th><th>Prénom</th><th>Notes prises en compte</th><th>Moyenne pondérée</th><th>Résultat</th></tr></thead>
                <tbody>
                <c:forEach var="r" items="${resultats}">
                    <tr>
                        <td><c:out value="${r.cne}"/></td>
                        <td><c:out value="${r.nom}"/></td>
                        <td><c:out value="${r.prenom}"/></td>
                        <td>${r.nombreNotes}</td>
                        <td><fmt:formatNumber value="${r.moyenne}" maxFractionDigits="2"/></td>
                        <td><c:out value="${r.resultat}"/></td>
                    </tr>
                </c:forEach>
                <c:if test="${empty resultats}"><tr><td colspan="6">Aucun étudiant inscrit à cette promotion.</td></tr></c:if>
                </tbody>
            </table>
        </div>
    </section>
</div>
</body>
</html>
