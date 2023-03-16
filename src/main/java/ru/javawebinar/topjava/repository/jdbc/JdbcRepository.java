package ru.javawebinar.topjava.repository.jdbc;

import javax.validation.*;
import java.util.Set;

public class JdbcRepository {
    protected ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

    protected <T> void validateEntiry(T entity) {
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<T>> errors = validator.validate(entity);

        if (!errors.isEmpty()) { throw new ConstraintViolationException(errors); }

    }
}
