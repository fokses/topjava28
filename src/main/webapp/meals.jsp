<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="https://topjava.ru/functions" prefix="f" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<jsp:useBean id="meals" scope="request" type="java.util.ArrayList"/>
<html lang="ru">
<head>
    <title>Meals</title>
    <link crossorigin="anonymous" media="all" rel="stylesheet"
          href="${pageContext.servletContext.contextPath}/style.css"/>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>Meals</h2>
<p><a href="meals?action=insert">Add Meal</a></p>
<table id="meals">
    <thead>
    <tr>
        <td>DateTime</td>
        <td>Description</td>
        <td>Calories</td>
        <td></td>
        <td></td>
    </tr>
    </thead>
    <c:forEach var="meal" items="${meals}">
        <jsp:useBean id="meal"  type="ru.javawebinar.topjava.model.MealTo"/>
        <tr class="<c:if test="${meal.excess}">excess</c:if>">
            <td>${f:formatLocalDateTime(meal.dateTime)}</td>
            <td>${meal.description}</td>
            <td>${meal.calories}</td>
            <td><a href="${pageContext.servletContext.contextPath}/meals?action=edit&mealId=${meal.id}">Edit</a></td>
            <td><a href="${pageContext.servletContext.contextPath}/meals?action=delete&mealId=${meal.id}">Delete</a>
            </td>

        </tr>
    </c:forEach>
</table>
</body>
</html>