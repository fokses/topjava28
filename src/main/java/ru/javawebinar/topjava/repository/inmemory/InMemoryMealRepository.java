package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static ru.javawebinar.topjava.util.UsersUtil.ADMIN_ID;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private final Map<Integer, Map<Integer, Meal>> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        for (Meal m : MealsUtil.meals)
            save(ADMIN_ID, m);
    }

    @Override
    public Meal save(int userId, Meal meal) {
        Map<Integer, Meal> userDb = getUserDb(userId);
        Objects.requireNonNull(userDb);
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            userDb.put(meal.getId(), meal);
            return meal;
        }
        // handle case: update, but not present in storage
        return userDb.computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
    }

    @Override
    public boolean delete(int userId, int id) {

        Map<Integer, Meal> userDb = getUserDb(userId);
        Objects.requireNonNull(userDb);
        return userDb.remove(id) != null;
    }

    @Override
    public Meal get(int userId, int id) {
        Map<Integer, Meal> userDb = getUserDb(userId);
        Objects.requireNonNull(userDb);
        return userDb.get(id);
    }

    @Override
    public List<Meal> getAll(int userId, LocalDate start, LocalDate end) {
        Map<Integer, Meal> userDb = getUserDb(userId);
        Objects.requireNonNull(userDb);
        return userDb.values().stream()
                .filter(m -> DateTimeUtil.isBetween(m.getDate(), start, end))
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Meal> getAll(int userId) {
        return getAll(userId, LocalDate.MIN, LocalDate.MAX);
    }

    private Map<Integer, Meal> getUserDb(int userId) {
        return repository.computeIfAbsent(userId, k -> new ConcurrentHashMap<>());
    }
}

