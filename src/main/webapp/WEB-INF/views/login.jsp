<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Connexion</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="auth-page">
<div class="auth-box">
    <h1>Gestion des notes</h1>
    <h2>Connexion</h2>
    <c:if test="${not empty error}">
        <div class="alert error">${error}</div>
    </c:if>
    <form action="${pageContext.request.contextPath}/login" method="post">
        <label for="username">Nom d'utilisateur</label>
        <input id="username" type="text" name="username" required>

        <label for="password">Mot de passe</label>
        <input id="password" type="password" name="password" required>

        <button type="submit">Se connecter</button>
    </form>
</div>
</body>
</html>
