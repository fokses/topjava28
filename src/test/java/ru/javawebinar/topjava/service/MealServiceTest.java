package ru.javawebinar.topjava.service;

import com.sun.org.apache.bcel.internal.generic.NEW;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.repository.inmemory.InMemoryMealRepository;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Comparator;
import java.util.stream.Collectors;

import static ru.javawebinar.topjava.UserTestData.ADMIN_ID;
import static ru.javawebinar.topjava.UserTestData.USER_ID;

import static org.junit.Assert.*;

@ContextConfiguration("classpath:spring/spring-app.xml")
@RunWith(SpringRunner.class)
public class MealServiceTest {

    @Autowired
    MealService service;

    @Autowired
    InMemoryMealRepository repository;

    @Before
    public void setup() {
        repository.init();
    }

    @Test
    public void get() {
        assertThrows(NotFoundException.class, () -> service.get(999, USER_ID));
        assertNotNull(service.get(1, USER_ID));
    }

    @Test
    public void delete() {
        service.delete(1, USER_ID);
        assertThrows(NotFoundException.class, () -> service.get(1, USER_ID));
    }

    @Test
    public void getBetweenInclusive() {
        assertEquals(
            service.getBetweenInclusive(LocalDate.of(2020, Month.JANUARY, 30), LocalDate.of(2020, Month.JANUARY, 31), USER_ID),
                MealsUtil.meals.stream().sorted(Comparator.comparing(Meal::getDateTime).reversed()).collect(Collectors.toList()));
    }

    @Test
    public void getAll() {
        Assert.assertEquals(
            MealsUtil.meals.stream().sorted(Comparator.comparing(Meal::getDateTime).reversed()).collect(Collectors.toList()),
            service.getAll(USER_ID)
        );
    }

    @Test
    public void create() {
        Meal m = new Meal(LocalDateTime.now(), "ПЫЩ", 2000);
        service.create(m, USER_ID);

        Assert.assertSame(m, service.get(m.getId(), USER_ID));
    }

    @Test
    public void update() {
        final int MEAL_ID = 1;
        final int NEW_CALORIES = 5000;
        Meal m = service.get(MEAL_ID, USER_ID);

        Meal n = new Meal(m.getId(), m.getDateTime(), m.getDescription(), NEW_CALORIES);

        service.update(n, USER_ID);
        Assert.assertEquals(service.get(MEAL_ID, USER_ID).getCalories(), NEW_CALORIES);
    }
}