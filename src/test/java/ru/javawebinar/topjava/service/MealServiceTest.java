package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.UserTestData;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.MealTestData.*;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@ActiveProfiles("jdbc")
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {

    static {
        // Only for postgres driver logging
        // It uses java.util.logging and logged via jul-to-slf4j bridge
        SLF4JBridgeHandler.install();
    }

    public static final int ADMIN_ID = UserTestData.ADMIN_ID;
    public static final int USER_ID = UserTestData.USER_ID;
    public static final int NON_EXISTED_ID = 999;
    @Autowired
    MealService service;

    @Test
    public void get() {
        assertMatch(service.get(USER_START_ID + 1, USER_ID),
                userMeal1);
    }

    @Test
    public void getNotExisted() {
        assertThrows(NotFoundException.class, () -> service.get(NON_EXISTED_ID, USER_ID));
    }

    @Test
    public void getOtherMeal() {
        assertThatNoException().isThrownBy(() -> service.get(ADMIN_START_ID + 1, ADMIN_ID));
        assertThrows(NotFoundException.class, () -> service.get(ADMIN_START_ID + 1, USER_ID));
    }

    @Test
    public void delete() {
        service.delete(ADMIN_START_ID + 1, ADMIN_ID);
        List<Meal> adminMeals = service.getAll(ADMIN_ID);
        assertMatch(adminMeals, adminMeal2);
        assertThrows(NotFoundException.class, () -> service.get(ADMIN_START_ID + 1, ADMIN_ID));

        service.delete(USER_START_ID + 1, USER_ID);
        assertMatch(service.getAll(USER_ID), Arrays.asList(userMeal7, userMeal6, userMeal5, userMeal4, userMeal3, userMeal2));
        assertThrows(NotFoundException.class, () -> service.get(USER_START_ID + 1, USER_ID));
    }

    @Test
    public void deleteNotFound() {
        assertThrows(NotFoundException.class, () -> service.delete(NON_EXISTED_ID, USER_ID));
    }

    @Test
    public void getBetweenInclusive() {
        List<Meal> betweenInclusive = service.getBetweenInclusive(LocalDate.of(2020, Month.JANUARY, 31), null, USER_ID);
        assertMatch(betweenInclusive, Arrays.asList(userMeal7, userMeal6, userMeal5, userMeal4));
    }

    @Test
    public void getAll() {
        List<Meal> list = service.getAll(ADMIN_ID);

        assertMatch(list, Arrays.asList(adminMeal2, adminMeal1));
        assertThat(list).isSortedAccordingTo(Comparator.comparing(Meal::getDateTime).reversed());

        assertMatch(service.getAll(USER_ID), Arrays.asList(userMeal7, userMeal6, userMeal5, userMeal4, userMeal3, userMeal2, userMeal1));
    }

    @Test
    public void update() {
        Meal updated = getUpdated();
        service.update(updated, USER_ID);
        assertMatch(service.get(updated.getId(), USER_ID), getUpdated());
    }

    @Test
    public void updateNonExistent() {
        Meal updated = getUpdated();
        updated.setId(NON_EXISTED_ID);
        assertThrows(NotFoundException.class, () -> service.update(updated, USER_ID));
    }

    @Test
    public void create() {
        Meal m1 = getNew();
        service.create(m1, USER_ID);
        Meal m2 = getNew();
        m2.setId(m1.getId());

        assertMatch(m1, m2);
        assertMatch(service.get(m1.getId(), USER_ID), m2);
    }

    @Test
    public void createDuplicateKeyDateTime() {
        Meal m1 = getNew();
        m1.setDateTime(userMeal1.getDateTime());

        assertThrows(DuplicateKeyException.class, () -> service.create(m1, USER_ID));
    }
}