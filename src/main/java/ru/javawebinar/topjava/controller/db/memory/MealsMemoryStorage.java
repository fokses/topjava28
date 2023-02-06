package ru.javawebinar.topjava.controller.db.memory;

import ru.javawebinar.topjava.controller.db.MealStorage;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.MealsUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MealsMemoryStorage implements MealStorage {
    private final AtomicInteger counter = new AtomicInteger(0);
    private final Map<Integer, Meal> db = new ConcurrentHashMap<>();

    public MealsMemoryStorage() {
        for (Meal m : MealsUtil.meals) {
            db.put(m.getId(), m);
        }
    }

    @Override
    public void add(Meal meal) {
        int mealId = counter.incrementAndGet();
        meal.setId(mealId);
        db.put(mealId, meal);
    }

    @Override
    public void delete(int mealId) {
        db.remove(mealId);
    }

    @Override
    public void update(int mealId, Meal meal) {
        meal.setId(mealId);
        db.computeIfPresent(mealId, (k, m) -> meal);
    }

    @Override
    public List<Meal> getAll() {
        return new ArrayList<>(db.values());
    }

    @Override
    public Meal getById(int mealId) {
        return db.get(mealId);
    }
}
