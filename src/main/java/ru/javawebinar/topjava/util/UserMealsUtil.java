package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        final List<UserMealWithExcess> result = new ArrayList<>();
        final Map<LocalDate, Integer> excessTable = new HashMap<>();
        final AtomicInteger counter = new AtomicInteger();

        synchronized (result) {
            for (UserMeal meal : meals) {
                excessTable.merge(meal.getDateTime().toLocalDate(), meal.getCalories(), Integer::sum);
                if (TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime)) {
                    new Thread(() -> {
                        synchronized (result) {
                            result.add(new UserMealWithExcess(meal, excessTable.getOrDefault(meal.getDateTime().toLocalDate(), 0) > caloriesPerDay));
                            counter.incrementAndGet();
                            result.notify();
                        }
                    }).start();
                } else {
                    counter.incrementAndGet();
                }
            }
        }

        synchronized (result) {
            while (counter.get() < meals.size()) {
                try {
                    result.wait();
                } catch (InterruptedException ignore) {}
            }
        }

        return result;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> excessTable = meals.stream().collect(Collectors.groupingBy(x -> x.getDateTime().toLocalDate(), Collectors.summingInt(x -> x.getCalories())));

        return meals.stream()
                .filter(x -> TimeUtil.isBetweenHalfOpen(x.getDateTime().toLocalTime(), startTime, endTime))
                .map(x -> new UserMealWithExcess(x, excessTable.getOrDefault(x.getDateTime().toLocalDate(), 0) > caloriesPerDay))
                .collect(Collectors.toList());
    }
}
