package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.javawebinar.topjava.controller.MealService;
import ru.javawebinar.topjava.controller.db.memory.MealsMemoryStorage;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.DateUtil;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;

public class MealServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(MealServlet.class);
    public static final String MEAL_JSP = "meal.jsp";
    public static final String MEALS_JSP = "meals.jsp";
    private final MealService service = new MealService(new MealsMemoryStorage()); //memory

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String forward = MEALS_JSP;
        String action = req.getParameter("action");
        log.debug("MealServlet get action = {}", action);

        if (Objects.nonNull(action) && action.equalsIgnoreCase("delete")) {
            int mealId = Integer.parseInt(req.getParameter("mealId"));
            service.delete(mealId);
            resp.sendRedirect(req.getContextPath() + "/meals");
            return;
        } else if (Objects.nonNull(action) && action.equalsIgnoreCase("edit")) {
            int mealId = Integer.parseInt(req.getParameter("mealId"));
            forward = MEAL_JSP;
            req.setAttribute("meal", service.getById(mealId));
        } else if (Objects.nonNull(action) && action.equalsIgnoreCase("insert")) {
            forward = MEAL_JSP;
        } else {
            req.setAttribute("meals", MealsUtil.filteredByStreams(service.getAll(), null, null, MealsUtil.CALORIES_PER_DAY));
        }

        req.getRequestDispatcher(forward).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        Integer id = WebUtil.getParam(req, "mealId", Integer::parseInt);
        LocalDateTime dateTime = WebUtil.getParam(req, "dateTime", DateUtil::fromString);
        String description = req.getParameter("description");
        Integer calories = WebUtil.getParam(req, "calories", Integer::parseInt, 0);

        Meal meal = new Meal(id, dateTime, description, calories);

        if (meal.isNew()) {
            service.add(meal);
        } else {
            service.update(meal);
        }

        req.setAttribute("meals", MealsUtil.filteredByStreams(service.getAll(), null, null, MealsUtil.CALORIES_PER_DAY));
        req.getRequestDispatcher(MEALS_JSP).forward(req, resp);
    }
}
