package ru.javawebinar.topjava.controller;

import ru.javawebinar.topjava.controller.db.MealStorage;
import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public class MealService {
    private final MealStorage storage;

    public MealService(MealStorage storage) {
        this.storage = storage;
    }

    public void add(Meal meal) {
        storage.add(meal);
    }

    public void delete(int mealId) {
        storage.delete(mealId);
    }

    public void update(Meal meal) {
        storage.update(meal.getId(), meal);
    }

    public List<Meal> getAll() {
        return storage.getAll();
    }

    public Meal getById(int mealId) {
        return storage.getById(mealId);
    }

}