package ru.yandex.practicum.interaction.api.exception;

public class AnotherServiceBadRequestException extends RuntimeException {
    public AnotherServiceBadRequestException(String message) {
        super(message);
    }
}
