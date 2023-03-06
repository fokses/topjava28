package ru.javawebinar.topjava.service.DataJpa;

import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.UserTestData;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.service.UserServiceTest;

import static ru.javawebinar.topjava.UserTestData.USER_ID;
import static ru.javawebinar.topjava.UserTestData.USER_MATCHER;
import static org.assertj.core.api.Assertions.assertThat;


@ActiveProfiles("datajpa")
public class DataJpaUserServiceTest extends UserServiceTest {
    @Test
    public void getWithMeals() {
        User user = service.getWithMeals(USER_ID);
        USER_MATCHER.assertMatch(user, UserTestData.user);
        assertThat(user.getMeals()).hasSize(MealTestData.meals.size());
    }
}
