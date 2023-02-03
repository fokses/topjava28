<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="https://topjava.ru/functions" prefix="f" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html lang="ru">
<head>
    <title>Meals</title>
    <link crossorigin="anonymous" media="all" rel="stylesheet" href="${pageContext.servletContext.contextPath}/style.css" /></head>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>Users</h2>
<table id="meals">
<thead>
<tr>
    <td>DateTime</td>
    <td>Description</td>
    <td>Calories</td>
</tr>
</thead>
<c:forEach var="meal" items="${meals}">
<tr class="<c:if test="${meal.excess}">excess</c:if>">
    <td>${f:formatLocalDateTime(meal.dateTime)}</td>
    <td>${meal.description}</td>
    <td>${meal.calories}</td>
</tr>
</c:forEach>
</table>
</body>
</html>