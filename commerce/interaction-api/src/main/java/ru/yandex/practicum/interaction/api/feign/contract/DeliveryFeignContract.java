package ru.yandex.practicum.interaction.api.feign.contract;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.interaction.api.dto.delivery.DeliveryDto;
import ru.yandex.practicum.interaction.api.dto.order.OrderDto;

import java.util.UUID;

public interface DeliveryFeignContract {

    DeliveryDto planDelivery(@RequestBody DeliveryDto deliveryDto);

    void deliverySuccessful(@RequestBody UUID deliveryId);

    void deliveryPicked(@RequestBody UUID deliveryId);

    void deliveryFailed(@RequestBody UUID deliveryId);

    Double deliveryCost(@RequestBody OrderDto orderDto);
}