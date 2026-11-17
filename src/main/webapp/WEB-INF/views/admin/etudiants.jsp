<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Gestion des étudiants</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=20261117-admin-ui">
</head>
<body class="student-management-page">
<div class="container">
    <header class="topbar">
        <div>
            <h1>Gestion des étudiants</h1>
            <p>Créez et gérez les profils étudiants et leurs comptes d'accès.</p>
        </div>
        <nav><a class="btn" href="${pageContext.request.contextPath}/admin">Tableau de bord</a>
            <a class="btn danger" href="${pageContext.request.contextPath}/logout">Déconnexion</a></nav>
    </header>
    <c:if test="${not empty param.message}"><div class="alert success"><c:out value="${param.message}"/></div></c:if>
    <c:if test="${not empty error}"><div class="alert error"><c:out value="${error}"/></div></c:if>
    <main class="dashboard-grid">
        <section class="card">
            <h2><c:choose><c:when test="${editing}">Modifier l'étudiant</c:when><c:otherwise>Ajouter un étudiant</c:otherwise></c:choose></h2>
            <form action="${pageContext.request.contextPath}/admin/etudiants" method="post">
                <input type="hidden" name="csrfToken" value="${csrfToken}">
                <c:if test="${editing}"><input type="hidden" name="id" value="${etudiant.id}"></c:if>
                <div class="form-grid">
                    <div class="field">
                        <label for="cne">CNE</label>
                        <input id="cne" name="cne" value="<c:out value='${formCne != null ? formCne : etudiant.idEtudiant}'/>" required>
                    </div>
                    <div class="field">
                        <label for="nom">Nom</label>
                        <input id="nom" name="nom" value="<c:out value='${formNom != null ? formNom : etudiant.nom}'/>" required>
                    </div>
                    <div class="field">
                        <label for="prenom">Prénom</label>
                        <input id="prenom" name="prenom" value="<c:out value='${formPrenom != null ? formPrenom : etudiant.prenom}'/>" required>
                    </div>
                    <div class="field">
                        <label for="dateNaissance">Date de naissance</label>
                        <input id="dateNaissance" type="date" name="dateNaissance" value="<c:out value='${formDate != null ? formDate : etudiant.dateNaissance}'/>">
                    </div>
                    <div class="field">
                        <label for="sexe">Sexe</label>
                        <select id="sexe" name="sexe" required>
                            <option value="">-- Sélectionner --</option>
                            <option value="F" ${formSexe == 'F' || (formSexe == null && etudiant.sexe == 'F') ? 'selected' : ''}>F</option>
                            <option value="M" ${formSexe == 'M' || (formSexe == null && etudiant.sexe == 'M') ? 'selected' : ''}>M</option>
                        </select>
                    </div>
                    <div class="field">
                        <label for="nomUtilisateur">Nom d'utilisateur</label>
                        <input id="nomUtilisateur" name="nomUtilisateur" value="<c:out value='${formUsername != null ? formUsername : etudiant.utilisateur.nomUtilisateur}'/>" required>
                    </div>
                    <div class="field">
                        <label for="email">Email</label>
                        <input id="email" type="email" name="email" value="<c:out value='${formEmail != null ? formEmail : etudiant.utilisateur.email}'/>" required>
                    </div>
                    <div class="field">
                        <label for="motDePasse">Mot de passe <c:if test="${editing}">(laisser vide pour le conserver)</c:if></label>
                        <input id="motDePasse" type="password" name="motDePasse" autocomplete="new-password" <c:if test="${not editing}">required</c:if>>
                    </div>
                </div>
                <div class="form-actions">
                    <button type="submit"><c:choose><c:when test="${editing}">Enregistrer les modifications</c:when><c:otherwise>Ajouter l'étudiant</c:otherwise></c:choose></button>
                    <c:if test="${editing}"><a class="btn secondary" href="${pageContext.request.contextPath}/admin/etudiants">Annuler</a></c:if>
                </div>
            </form>
        </section>
        <section class="card wide">
            <h2>Étudiants enregistrés <span class="metric-label">(${etudiants.size()})</span></h2>
            <div class="table-wrap"><table>
                <thead><tr><th>CNE</th><th>Nom</th><th>Prénom</th><th>Email</th><th>Compte</th><th>Actions</th></tr></thead>
                <tbody>
                <c:forEach var="e" items="${etudiants}">
                    <tr><td><c:out value="${e.idEtudiant}"/></td><td><c:out value="${e.nom}"/></td>
                        <td><c:out value="${e.prenom}"/></td><td><c:out value="${e.utilisateur.email}"/></td>
                        <td><c:out value="${e.utilisateur.nomUtilisateur}"/></td>
                        <td class="actions">
                            <a class="btn secondary" href="${pageContext.request.contextPath}/admin/etudiants?id=${e.id}">Modifier</a>
                            <form class="inline-form" action="${pageContext.request.contextPath}/admin/etudiants" method="post"
                                  onsubmit="return confirm('Supprimer cet étudiant ? Les inscriptions existantes empêchent la suppression.');">
                                <input type="hidden" name="csrfToken" value="${csrfToken}">
                                <input type="hidden" name="operation" value="delete"><input type="hidden" name="id" value="${e.id}">
                                <button class="btn danger" type="submit">Supprimer</button>
                            </form>
                        </td></tr>
                </c:forEach>
                <c:if test="${empty etudiants}"><tr><td class="table-empty" colspan="6">Aucun étudiant n'est encore enregistré.</td></tr></c:if>
                </tbody>
            </table></div>
        </section>
    </main>
</div>
</body>
</html>
