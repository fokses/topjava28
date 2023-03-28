package ru.javawebinar.topjava.web.meal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.util.exception.NotFoundException;
import ru.javawebinar.topjava.web.AbstractControllerTest;
import ru.javawebinar.topjava.web.SecurityUtil;
import ru.javawebinar.topjava.web.json.JsonUtil;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;
import static ru.javawebinar.topjava.web.meal.MealRestController.REST_MEALS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.javawebinar.topjava.MealTestData.*;

class MealRestControllerTest extends AbstractControllerTest {

    @Autowired
    private MealService mealService;

    @Test
    void getAll() throws Exception {
        perform(get(REST_MEALS))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MEALTO_MATCHER.contentJson(MealsUtil.getTos(meals, SecurityUtil.authUserCaloriesPerDay())));
    }

    @Test
    void getMeal() throws Exception {

        perform(get(getUri(MEAL1_ID)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MEAL_MATCHER.contentJson(meal1));

    }

    @Test
    void createWithLocation() throws Exception {
        Meal m = getNew();

        ResultActions action = perform(post(REST_MEALS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(m)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        Meal created = MEAL_MATCHER.readFromJson(action);
        m.setId(created.getId());

        MEAL_MATCHER.assertMatch(created, m);
        MEAL_MATCHER.assertMatch(mealService.get(created.id(), SecurityUtil.authUserId()), m);

    }

    @Test
    void update() throws Exception {
        Meal u = getUpdated();

        perform(put(getUri(u.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(u)))
                .andExpect(status().isNoContent());

        MEAL_MATCHER.assertMatch(mealService.get(MEAL1_ID, SecurityUtil.authUserId()), u);

    }

    @Test
    void deleteMeal() throws Exception {
        perform(delete(getUri(MEAL1_ID)))
                .andExpect(status().isNoContent());

        assertThrows(NotFoundException.class, () -> mealService.get(MEAL1_ID, SecurityUtil.authUserId()));
    }

    @Test
    void getBetween() throws Exception {
        URI between = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_MEALS)
                .path("/between")
                .queryParam("start", startDateTime)
                .queryParam("end", endDateTime)
                .build().toUri();

        perform(get(between))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MEALTO_MATCHER.contentJson(
                        MealsUtil.getFilteredTos(meals, SecurityUtil.authUserCaloriesPerDay(),
                            startDateTime.toLocalTime(), endDateTime.toLocalTime())
                        )
                );
    }

    private URI getUri(Integer u) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_MEALS)
                .path("/{id}")
                .buildAndExpand(u).toUri();
    }
}