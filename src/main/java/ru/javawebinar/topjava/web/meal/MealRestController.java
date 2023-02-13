package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static ru.javawebinar.topjava.util.ValidationUtil.assureIdConsistent;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNew;

@Controller
public class MealRestController {
    private  final Logger log = LoggerFactory.getLogger(getClass());
    private final MealService service;

    public MealRestController(MealService service) {
        this.service = service;
    }

    public List<MealTo> getAll() {
        log.info("getAll: {}", SecurityUtil.authUserId());
        return MealsUtil.getTos(service.getAll(SecurityUtil.authUserId()), SecurityUtil.authUserCaloriesPerDay());
    }

    public List<MealTo> getAll(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
        log.info("getAll: {}; filters {} {} {} {}", SecurityUtil.authUserId(), startDate, endDate, startTime, endTime);


        return MealsUtil.getFilteredTos(
                service.getAll(SecurityUtil.authUserId(),
                        (startDate == null ? LocalDate.MIN : startDate), (endDate == null ? LocalDate.MAX : endDate)),
                SecurityUtil.authUserCaloriesPerDay(),
                (startTime == null ? LocalTime.MIN : startTime),
                (endTime == null ? LocalTime.MAX : endTime)
        );

    }

    public Meal get(int mealId) {
        return service.get(SecurityUtil.authUserId(), mealId);
    }

    public void delete(int mealId) {
        log.info("delete: {}", mealId);
        service.delete(SecurityUtil.authUserId(), mealId);
    }

    public Meal create(Meal m) {
        log.info("create: {}", m);
        checkNew(m);
        return service.create(SecurityUtil.authUserId(), m);
    }

    public void update(Meal m) {
        log.info("update meal {} with id {}", m, m.getId());
        assureIdConsistent(m, m.getId());
        service.update(SecurityUtil.authUserId(), m);
    }
}