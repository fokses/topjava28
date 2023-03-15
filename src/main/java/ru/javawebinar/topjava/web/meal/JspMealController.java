package ru.javawebinar.topjava.web.meal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.web.MealController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalDate;
import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalTime;

@Controller
public class JspMealController extends MealController {

    protected JspMealController(MealService service) {
        super(service);
    }

    @GetMapping("/meals")
    public String getMeals(Model model, HttpServletRequest request) {
        log.info("meals");
        model.addAttribute("meals", getBetween(
                parseLocalDate(request.getParameter("startDate")),
                parseLocalTime(request.getParameter("startTime")),
                parseLocalDate(request.getParameter("endDate")),
                parseLocalTime(request.getParameter("endTime"))
        ));
        return "meals";
    }

    @GetMapping("/meals/delete")
    public String delete(HttpServletRequest request) {
        int id = getId(request);
        log.info("delete {}", id);

        delete(id);

        return "redirect:/meals";
    }

    @GetMapping("/meals/update")
    public String update(Model model, HttpServletRequest request) {
        int id = getId(request);
        log.info("update {}", id);

        model.addAttribute("meal", get(id));

        return "mealForm";
    }

    @GetMapping("/meals/create")
    public String create(Model model) {

        model.addAttribute("meal",
                new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000));

        return "mealForm";
    }

    @PostMapping("/meals")
    public String save(HttpServletRequest request) throws UnsupportedEncodingException {
        Meal meal = new Meal(
                LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories")));

        log.info("save {}", meal);

        if (StringUtils.hasLength(request.getParameter("id"))) {
            update(meal, getId(request));
        } else {
            create(meal);
        }
        return "redirect:/meals";
    }

    private int getId(HttpServletRequest request) {
        String paramId = Objects.requireNonNull(request.getParameter("id"));
        return Integer.parseInt(paramId);
    }

}
