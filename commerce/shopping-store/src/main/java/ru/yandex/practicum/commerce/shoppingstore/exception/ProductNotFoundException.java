package ru.yandex.practicum.commerce.shoppingstore.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
public class ProductNotFoundException extends RuntimeException {
    private HttpStatus httpStatus;
    private String userMessage;

    public ProductNotFoundException(String message,  Throwable cause, String userMessage, HttpStatus httpStatus) {
        super(message, cause);
        this.userMessage = userMessage;
        this.httpStatus = httpStatus;
    }

    public List<StackTraceElement> getFullStackTrace() {
        return List.of(this.getStackTrace());
    }
}