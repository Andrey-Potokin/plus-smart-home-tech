package ru.yandex.practicum.commerce.delivery.service;


import ru.yandex.practicum.interaction.api.dto.delivery.DeliveryDto;
import ru.yandex.practicum.interaction.api.dto.order.OrderDto;

import java.util.UUID;

public interface DeliveryService {

    DeliveryDto planDelivery(DeliveryDto deliveryDto);

    void deliverySuccessful(UUID deliveryId);

    void deliveryPicked(UUID deliveryId);

    void deliveryFailed(UUID deliveryId);

    Double deliveryCost(OrderDto orderDto);
}