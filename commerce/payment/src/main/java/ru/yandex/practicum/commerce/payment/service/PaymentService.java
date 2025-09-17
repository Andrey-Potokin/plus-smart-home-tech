package ru.yandex.practicum.commerce.payment.service;

import ru.yandex.practicum.interaction.api.dto.order.OrderDto;
import ru.yandex.practicum.interaction.api.dto.payment.PaymentDto;

import java.util.UUID;

public interface PaymentService {

    PaymentDto createPayment(OrderDto orderDto);

    Double getTotalCost(OrderDto orderDto);

    void paymentSuccess(UUID uuid);

    Double productCost(OrderDto orderDto);

    void paymentFailed(UUID uuid);
}