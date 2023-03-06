package ru.javawebinar.topjava.repository.datajpa;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.util.List;

@Transactional(readOnly = true)
public interface CrudMealRepository extends JpaRepository<Meal, Integer> {
    @Query("SELECT m from Meal m where m.user.id = :userId")
    List<Meal> findAllByUserId(Sort sort, @Param("userId") int userId);

    List<Meal> findAllByDateTimeGreaterThanEqualAndDateTimeLessThanAndUserId(Sort sort, LocalDateTime startDateTime, LocalDateTime endDateTime, int userId);

    @Transactional
    @Modifying
    int deleteMealByIdAndUserId(int id, int userId);

    @Query("select m from Meal m join fetch m.user where m.id = ?1 and m.user.id = ?2")
    Meal getWithUser(int id, int userId);
}
