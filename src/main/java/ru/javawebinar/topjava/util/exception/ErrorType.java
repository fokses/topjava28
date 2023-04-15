package ru.javawebinar.topjava.util.exception;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ErrorType {
    APP_ERROR("Application error"),
    DATA_NOT_FOUND("Data not found"),
    DATA_ERROR("Data error"),
    USER_MAIL_DUPLICATE("User mail duplicate"),
    VALIDATION_ERROR("Data validation error");

    private final String description;

    ErrorType(String description) {
        this.description = description;
    }

    @JsonValue
    public String getDescription() {
        return description;
    }
}
