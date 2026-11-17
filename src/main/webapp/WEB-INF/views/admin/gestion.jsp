<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><c:out value="${title}"/> — Gestion des notes</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=20261117-prof-filiere">
</head>
<body class="admin-management-page admin-management-${type}">
<div class="container">
    <header class="topbar">
        <div>
            <h1><c:out value="${title}"/></h1>
            <p>Gérez les données et consultez les enregistrements existants.</p>
        </div>
        <nav><a class="btn" href="${pageContext.request.contextPath}/admin">Tableau de bord</a>
            <a class="btn danger" href="${pageContext.request.contextPath}/logout">Déconnexion</a></nav>
    </header>
    <c:if test="${not empty param.message}"><div class="alert success"><c:out value="${param.message}"/></div></c:if>
    <c:if test="${not empty error}"><div class="alert error"><c:out value="${error}"/></div></c:if>

    <main class="dashboard-grid">
        <section class="card">
            <h2>
                <c:choose>
                    <c:when test="${type == 'professeurs'}">
                        <c:choose><c:when test="${editing}">Modifier un professeur</c:when><c:otherwise>Ajouter un professeur</c:otherwise></c:choose>
                    </c:when>
                    <c:when test="${type == 'filieres'}">
                        <c:choose><c:when test="${editing}">Modifier une filière</c:when><c:otherwise>Ajouter une filière</c:otherwise></c:choose>
                    </c:when>
                    <c:otherwise>
                        <c:choose><c:when test="${editing}">Modifier — <c:out value="${title}"/></c:when><c:otherwise>Ajouter — <c:out value="${title}"/></c:otherwise></c:choose>
                    </c:otherwise>
                </c:choose>
            </h2>
            <form action="${pageContext.request.contextPath}/admin/gestion" method="post"
                  autocomplete="${type == 'professeurs' ? 'off' : 'on'}">
                <input type="hidden" name="type" value="<c:out value='${type}'/>">
                <input type="hidden" name="csrfToken" value="${csrfToken}">
                <div class="form-grid">
                    <c:forEach var="field" items="${fields}">
                        <c:choose>
                            <c:when test="${field.type == 'hidden'}">
                                <input type="hidden" name="<c:out value='${field.name}'/>" value="<c:out value='${field.value}'/>">
                            </c:when>
                            <c:when test="${field.type == 'select'}">
                                <div class="field">
                                    <label for="<c:out value='${field.name}'/>"><c:out value="${field.label}"/></label>
                                    <select id="<c:out value='${field.name}'/>" name="<c:out value='${field.name}'/>" required>
                                        <option value="">-- Sélectionner --</option>
                                        <c:forEach var="option" items="${field.options}">
                                            <option value="<c:out value='${option.value}'/>" ${option.value == field.value ? 'selected' : ''}>
                                                <c:out value="${option.label}"/>
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="field">
                                    <label for="<c:out value='${field.name}'/>"><c:out value="${field.label}"/></label>
                                    <input id="<c:out value='${field.name}'/>" type="<c:out value='${field.type}'/>"
                                           name="<c:out value='${field.name}'/>" value="<c:out value='${field.value}'/>"
                                           <c:if test="${field.type == 'number'}">step="any"</c:if>
                                           <c:if test="${type == 'professeurs' && field.name == 'nomUtilisateur'}">autocomplete="off"</c:if>
                                           <c:if test="${type == 'professeurs' && field.name == 'motDePasse'}">autocomplete="new-password"</c:if>
                                           <c:if test="${field.required}">required</c:if>>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                </div>
                <div class="form-actions">
                    <button type="submit"><c:choose><c:when test="${editing}">Enregistrer les modifications</c:when>
                        <c:otherwise>Ajouter</c:otherwise></c:choose></button>
                    <c:if test="${editing}"><a class="btn secondary" href="${pageContext.request.contextPath}/admin/gestion?type=<c:out value='${type}'/>">Annuler</a></c:if>
                </div>
            </form>
        </section>

        <section class="card wide">
            <h2>
                <c:choose>
                    <c:when test="${type == 'professeurs'}">Professeurs enregistrés</c:when>
                    <c:when test="${type == 'filieres'}">Filières enregistrées</c:when>
                    <c:otherwise>Enregistrements</c:otherwise>
                </c:choose>
                <span class="metric-label">(${requestScope.adminRecordRows.size()})</span>
            </h2>
            <div class="table-wrap">
                <table>
                    <thead><tr><th>ID</th><c:forEach var="columnLabel" items="${requestScope.adminColumnLabels}"><th><c:out value="${columnLabel}"/></th></c:forEach><th>Actions</th></tr></thead>
                    <tbody>
                    <c:forEach var="row" items="${requestScope.adminRecordRows}">
                        <tr>
                            <td><c:out value="${row.id}"/></td>
                            <c:forEach var="cell" items="${row.cells}"><td><c:out value="${cell}"/></td></c:forEach>
                            <td class="actions">
                                <a class="btn secondary" href="${pageContext.request.contextPath}/admin/gestion?type=<c:out value='${type}'/>&amp;action=edit&amp;id=<c:out value='${row.id}'/>">Modifier</a>
                                <c:if test="${type == 'promotions'}">
                                    <a class="btn secondary" href="${pageContext.request.contextPath}/admin/resultats?promotionId=<c:out value='${row.id}'/>">Résultats</a>
                                </c:if>
                                <form class="inline-form" action="${pageContext.request.contextPath}/admin/gestion" method="post"
                                      onsubmit="return confirm('Supprimer cet enregistrement ? Les données associées empêchent la suppression si elles sont encore utilisées.');">
                                    <input type="hidden" name="type" value="<c:out value='${type}'/>">
                                    <input type="hidden" name="id" value="<c:out value='${row.id}'/>">
                                    <input type="hidden" name="operation" value="delete">
                                    <input type="hidden" name="csrfToken" value="${csrfToken}">
                                    <button class="btn danger" type="submit">Supprimer</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty requestScope.adminRecordRows}">
                        <tr>
                            <td class="table-empty" colspan="${requestScope.adminColumnCount}">
                                <c:choose>
                                    <c:when test="${type == 'professeurs'}">Aucun professeur enregistré.</c:when>
                                    <c:when test="${type == 'filieres'}">Aucune filière enregistrée.</c:when>
                                    <c:otherwise>Aucun enregistrement.</c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:if>
                    </tbody>
                </table>
            </div>
        </section>
    </main>
</div>
</body>
</html>
