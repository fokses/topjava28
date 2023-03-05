package ru.javawebinar.topjava.repository.datajpa;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.MealRepository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class DataJpaMealRepository implements MealRepository {

    private static final Sort SORT_DATE_TIME = Sort.by(Sort.Direction.DESC, "dateTime");

    private final CrudMealRepository mealRepository;
    private final CrudUserRepository userRepository;

    public DataJpaMealRepository(CrudMealRepository mealRepository, CrudUserRepository userRepository) {
        this.mealRepository = mealRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    @Modifying
    public Meal save(Meal meal, int userId) {
        User u = userRepository.getReferenceById(userId);
        meal.setUser(u);
        if (meal.isNew())
            return mealRepository.save(meal);

        if (meal.getId() == null || get(meal.getId(), userId) == null)
            return null;

        return mealRepository.save(meal);
    }

    @Override
    @Transactional
    @Modifying
    public boolean delete(int id, int userId) {
        return mealRepository.deleteMealByIdAndUserId(id, userId) != 0;
    }

    @Override
    public Meal get(int id, int userId) {
        Meal m = mealRepository.findById(id).orElse(null);

        if (m == null) { return null; }

        if (m.getUser().getId() == null || m.getUser().getId() != userId) { return null; }

        return m;
    }

    @Override
    public List<Meal> getAll(int userId) {
        return mealRepository.findAllByUserId(SORT_DATE_TIME, userId);
    }

    @Override
    public List<Meal> getBetweenHalfOpen(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId) {
        return mealRepository.findAllByDateTimeGreaterThanEqualAndDateTimeLessThanAndUserId(SORT_DATE_TIME, startDateTime, endDateTime, userId);
    }
}
