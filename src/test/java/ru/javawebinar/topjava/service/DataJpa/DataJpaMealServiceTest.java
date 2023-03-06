package ru.javawebinar.topjava.service.DataJpa;

import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.service.MealServiceTest;

import static ru.javawebinar.topjava.MealTestData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static ru.javawebinar.topjava.UserTestData.USER_ID;

@ActiveProfiles("datajpa")
public class DataJpaMealServiceTest extends MealServiceTest {
    @Test
    public void getWithUser() {
        Meal m = service.getWithUser(MEAL1_ID, USER_ID);
        MEAL_MATCHER.assertMatch(m, meal1);
        assertThat(m.getUser()).isNotNull();
        assertThat(m.getUser()).isExactlyInstanceOf(User.class);
    }
}
