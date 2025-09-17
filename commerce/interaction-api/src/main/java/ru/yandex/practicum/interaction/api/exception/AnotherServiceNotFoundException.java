package ru.yandex.practicum.interaction.api.exception;

public class AnotherServiceNotFoundException extends RuntimeException {
    public AnotherServiceNotFoundException(String message) {
        super(message);
    }
}
