package ru.javawebinar.topjava.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "Meal", uniqueConstraints = {
        @UniqueConstraint(name = "uc_meal_date_time_user_id", columnNames = {"date_time", "user_id"})
})
@NamedQueries({
        @NamedQuery(name = "Meal.GET", query = "SELECT m FROM Meal m WHERE m.id = :id AND m.user.id = :userId"),
        @NamedQuery(name = "Meal.GET_BETWEEN", query = "SELECT m FROM Meal m WHERE m.user.id = :userId AND m.dateTime >= :startDateTime " +
                "AND m.dateTime < :endDateTime ORDER BY m.dateTime DESC"),
        @NamedQuery(name = "Meal.GET_ALL", query = "SELECT m FROM Meal m WHERE m.user.id = :userId ORDER BY m.dateTime DESC"),
        @NamedQuery(name = "Meal.DELETE", query = "DELETE FROM Meal m WHERE m.id = :id AND m.user.id = :userId")
})
public class Meal extends AbstractBaseEntity {
    @NotNull
    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;

    @Column(nullable = false)
    @NotBlank
    private String description;

    @Column(nullable = false)
    private int calories;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private User user;

    public Meal() {
    }

    public Meal(LocalDateTime dateTime, String description, int calories) {
        this(null, dateTime, description, calories);
    }

    public Meal(Integer id, LocalDateTime dateTime, String description, int calories) {
        super(id);
        this.dateTime = dateTime;
        this.description = description;
        this.calories = calories;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getDescription() {
        return description;
    }

    public int getCalories() {
        return calories;
    }

    public LocalDate getDate() {
        return dateTime.toLocalDate();
    }

    public LocalTime getTime() {
        return dateTime.toLocalTime();
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Meal{" +
                "id=" + id +
                ", dateTime=" + dateTime +
                ", description='" + description + '\'' +
                ", calories=" + calories +
                '}';
    }
}
