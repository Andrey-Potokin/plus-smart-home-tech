package ru.yandex.practicum.commerce.order.client.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import jakarta.ws.rs.InternalServerErrorException;
import ru.yandex.practicum.interaction.api.exception.AnotherServiceBadRequestException;
import ru.yandex.practicum.interaction.api.exception.AnotherServiceNotFoundException;
import ru.yandex.practicum.interaction.api.exception.NotAuthorizedUserException;
import ru.yandex.practicum.interaction.api.exception.handler.ErrorResponse;

import java.io.IOException;
import java.io.InputStream;

public class CustomErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String s, Response response) {

        ErrorResponse errorResponse;

        try (InputStream body = response.body().asInputStream()) {
            ObjectMapper mapper = new ObjectMapper();
            errorResponse = mapper.readValue(body, ErrorResponse.class);
        } catch (IOException ex) {
            return new Exception(ex.getMessage());
        }

        return switch (response.status()) {
            case 400 -> new AnotherServiceBadRequestException(
                    errorResponse.getUserMessage() != null ? errorResponse.getUserMessage() : "Bad request"
            );
            case 401 -> new NotAuthorizedUserException(
                    errorResponse.getUserMessage() != null ? errorResponse.getUserMessage() : "Unauthorized"
            );
            case 404 -> new AnotherServiceNotFoundException(
                    errorResponse.getUserMessage() != null ? errorResponse.getUserMessage() : "Not found"
            );
            case 500 -> new InternalServerErrorException(
                    errorResponse.getUserMessage() != null ? errorResponse.getUserMessage() : "Internal server error"
            );
            default -> defaultDecoder.decode(s, response);
        };
    }
}