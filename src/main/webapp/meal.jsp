<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="https://topjava.ru/functions" prefix="f" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%--@elvariable id="meal" type="ru.javawebinar.topjava.model.Meal"--%>
<html lang="ru">
<head>
    <title>Meal</title>
    <link crossorigin="anonymous" media="all" rel="stylesheet"
          href="${pageContext.servletContext.contextPath}/style.css"/>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>Edit meal</h2>
<form method="post" id="meal" action="meals">
    <input type="hidden" readonly="readonly" name="mealId"
           value="<c:out value="${meal.id}" />"/> <br/>
    <label for="dateTime">Datetime :</label>
    <input
            type="datetime-local" name="dateTime" id="dateTime"
            value="${f:toString(meal.dateTime)}"/>
    <br/>
    <label for="description">Description :</label> <input
        type="text" name="description" id="description"
        value="<c:out value="${meal.description}"/>"/> <br/>
    <label for="calories">Calories :</label> <input
        type="text" name="calories" id="calories"
        value="<c:out value="${meal.calories}" />"/> <br/>
    <input type="submit" value="Submit"/>
    <button onclick="window.history.back()" type="button">Cancel</button>
</form>
</body>
</html>