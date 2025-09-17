package ru.yandex.practicum.interaction.api.exception;

public class ProductNotFoundInWarehouseException extends RuntimeException {
    public ProductNotFoundInWarehouseException(String message) {
        super(message);
    }
}