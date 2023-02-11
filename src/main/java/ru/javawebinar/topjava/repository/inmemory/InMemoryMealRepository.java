package ru.javawebinar.topjava.repository.inmemory;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class InMemoryMealRepository implements MealRepository {
    private final Map<Integer, Map<Integer, Meal>> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        for (Meal m : MealsUtil.meals)
            save(m, SecurityUtil.authUserId());
    }

    @Override
    public Meal save(Meal meal, int userId) {
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
    public boolean delete(int id, int userId) {

        Map<Integer, Meal> userDb = getUserDb(userId);
        Objects.requireNonNull(userDb);
        return userDb.remove(id) != null;
    }

    @Override
    public Meal get(int id, int userId) {
        Map<Integer, Meal> userDb = getUserDb(userId);
        Objects.requireNonNull(userDb);
        return userDb.get(id);
    }

    @Override
    public List<Meal> getAll(int userId) {
        Map<Integer, Meal> userDb = getUserDb(userId);
        Objects.requireNonNull(userDb);
        return userDb.values().stream()
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }

    private Map<Integer, Meal> getUserDb(int userId) {
        return repository.computeIfAbsent(userId, k -> new ConcurrentHashMap<>());
    }
}

