package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class JdbcMealRepository implements MealRepository {

    private static final BeanPropertyRowMapper<Meal> mapper = new BeanPropertyRowMapper<>(Meal.class);

    private final JdbcTemplate template;

    private final SimpleJdbcInsert insert;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public JdbcMealRepository(JdbcTemplate template, DataSource source) {
        this.template = template;

        insert = new SimpleJdbcInsert(source)
                .withTableName("meals")
                .usingGeneratedKeyColumns("id");

        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(source);
    }


    @Override
    public Meal save(Meal meal, int userId) {

        SqlParameterSource source = new MapSqlParameterSource()
                .addValue("id", meal.getId())
                .addValue("date_time", meal.getDateTime())
                .addValue("description", meal.getDescription())
                .addValue("calories", meal.getCalories())
                .addValue("user_id", userId);

        if (meal.isNew()) {
            Integer newId = (Integer) insert.executeAndReturnKey(source);
            meal.setId(newId);

            return meal;
        }

        if (namedParameterJdbcTemplate.update(
                "UPDATE meals SET date_time=:date_time, description=:description, calories=:calories WHERE id=:id AND user_id=:user_id",
                source) == 0)
            return null;

        return meal;
    }

    @Override
    public boolean delete(int id, int userId) {
        return template.update("DELETE FROM meals WHERE id = ? AND user_id = ?", id, userId) != 0;
    }

    @Override
    public Meal get(int id, int userId) {
        List<Meal> query = template.query("SELECT * FROM meals WHERE id=? AND user_id=?", mapper, id, userId);
        return DataAccessUtils.singleResult(query);
    }

    @Override
    public List<Meal> getAll(int userId) {
        return template.query("SELECT * FROM meals WHERE user_id=? ORDER BY date_time desc", mapper, userId);
    }

    @Override
    public List<Meal> getBetweenHalfOpen(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId) {
        return template.query("SELECT * FROM meals WHERE date_time >= ? AND date_time < ? ORDER BY date_time desc", mapper, startDateTime, endDateTime);
    }
}
