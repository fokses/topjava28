package ru.javawebinar.topjava.controller.db;

import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public interface MealStorage {
    void add(Meal meal);

    void delete(int mealId);

    void update(int mealId, Meal meal);

    List<Meal> getAll();

    Meal getById(int mealId);
}
