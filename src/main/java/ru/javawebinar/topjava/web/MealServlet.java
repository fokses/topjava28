package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.ServletUtil;
import ru.javawebinar.topjava.web.meal.MealRestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class MealServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(MealServlet.class);
    private ConfigurableApplicationContext appCtx;

    private MealRestController restController;

    @Override
    public void init() {
        appCtx = new ClassPathXmlApplicationContext("spring/spring-app.xml");
        restController = appCtx.getBean(MealRestController.class);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        String id = request.getParameter("id");

        Meal meal = new Meal(id.isEmpty() ? null : Integer.valueOf(id),
                Objects.requireNonNull(ServletUtil.getParam(request, "dateTime", LocalDateTime::parse)),
                request.getParameter("description"),
                Objects.requireNonNull(ServletUtil.getParam(request, "calories", Integer::parseInt))
        );

        log.info(meal.isNew() ? "Create {}" : "Update {}", meal);

        if (meal.isNew()) {
            restController.create(meal);
        } else {
            restController.update(meal);
        }
        response.sendRedirect("meals");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        switch (action == null ? "all" : action) {
            case "delete":
                Integer id = ServletUtil.getParam(request, "id", Integer::parseInt);
                Objects.requireNonNull(id);
                log.info("Delete id={}", id);
                restController.delete(id);
                response.sendRedirect("meals");
                break;
            case "create":
            case "update":
                final Meal meal = "create".equals(action) ?
                        new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000) :
                        restController.get(Objects.requireNonNull(ServletUtil.getParam(request, "id", Integer::parseInt)));
                request.setAttribute("meal", meal);
                request.getRequestDispatcher("/mealForm.jsp").forward(request, response);
                break;
            case "all":
            default:
                LocalDate startDate,endDate;
                request.setAttribute("startDate", startDate = ServletUtil.getParam(request, "startDate", DateTimeUtil::toLocalDate));
                request.setAttribute("endDate", endDate = ServletUtil.getParam(request, "endDate", DateTimeUtil::toLocalDate));

                LocalTime startTime, endTime;
                request.setAttribute("startTime", startTime = ServletUtil.getParam(request, "startTime", DateTimeUtil::toLocalTime));
                request.setAttribute("endTime", endTime = ServletUtil.getParam(request, "endTime", DateTimeUtil::toLocalTime));

                log.info("getAll {} - {} : {} - {}", startDate, endDate, startTime, endTime);

                request.setAttribute("meals", restController.getAll(startDate, endDate,
                        startTime, endTime));
                request.getRequestDispatcher("/meals.jsp").forward(request, response);
                break;
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        appCtx.close();
    }
}
