package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.javawebinar.topjava.UserTestData.GUEST_ID;

public class MealTestData {

    public static int USER_START_ID = GUEST_ID;

    public static int ADMIN_START_ID = USER_START_ID + 7;

    public static final Meal userMeal1 = new Meal(USER_START_ID + 1, LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0, 0),
        "Завтрак", 500);

    public static final Meal userMeal2 = new Meal(USER_START_ID + 2, LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0, 0),
            "Обед", 1000);

    public static final Meal userMeal3 = new Meal(USER_START_ID + 3, LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0, 0),
            "Ужин", 500);

    public static final Meal userMeal4 = new Meal(USER_START_ID + 4, LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0, 0),
            "Еда на граничное значение", 100);

    public static final Meal userMeal5 = new Meal(USER_START_ID + 5, LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0, 0),
            "Завтрак", 1000);

    public static final Meal userMeal6 = new Meal(USER_START_ID + 6, LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0, 0),
            "Обед", 500);

    public static final Meal userMeal7 = new Meal(USER_START_ID + 7, LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0, 0),
            "Ужин", 410);

    public static final Meal adminMeal1 = new Meal(ADMIN_START_ID + 1, LocalDateTime.of(2015, Month.JUNE, 1, 14, 0, 0),
            "Админ ланч", 510);

    public static final Meal adminMeal2 = new Meal(ADMIN_START_ID + 2, LocalDateTime.of(2015, Month.JUNE, 1, 21, 0, 0),
            "Админ ужин", 1500);

    public static Meal getNew() {
        return new Meal(LocalDateTime.of(2022, 2, 22, 0, 0, 0), "new Meal", 666);
    }

    public static Meal getUpdated() {
        Meal m = new Meal(userMeal1);
        m.setDateTime(LocalDateTime.of(2022, 1, 1, 10, 0, 0));
        m.setDescription("Updated");
        m.setCalories(909);
        return m;
    }

    public static void assertMatch(Meal actual, Meal expected) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    public static void assertMatch(Iterable<Meal> actual, Meal m) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(Arrays.asList(m));
    }

    public static void assertMatch(Iterable<Meal> actual, Iterable<Meal> expected) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
