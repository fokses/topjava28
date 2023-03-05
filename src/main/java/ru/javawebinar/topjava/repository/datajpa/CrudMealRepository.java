package ru.javawebinar.topjava.repository.datajpa;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.util.List;

public interface CrudMealRepository extends JpaRepository<Meal, Integer> {
    @Query("SELECT m from Meal m where m.user.id = :userId")
    List<Meal> findAllByUserId(Sort sort, @Param("userId") int userId);

    List<Meal> findAllByDateTimeGreaterThanEqualAndDateTimeLessThanAndUserId(Sort sort, LocalDateTime startDateTime, LocalDateTime endDateTime, int userId);

    int deleteMealByIdAndUserId(int id, int userId);
}
