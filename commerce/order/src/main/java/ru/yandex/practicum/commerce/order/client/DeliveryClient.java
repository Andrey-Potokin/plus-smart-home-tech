package ru.yandex.practicum.commerce.order.client;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.interaction.api.dto.delivery.DeliveryDto;
import ru.yandex.practicum.interaction.api.dto.order.OrderDto;
import ru.yandex.practicum.interaction.api.feign.contract.DeliveryFeignContract;

import java.util.UUID;

@FeignClient(name = "delivery", path = "/api/v1/delivery")
public interface DeliveryClient extends DeliveryFeignContract {

    @Override
    @PutMapping
    DeliveryDto planDelivery(@RequestBody @Valid DeliveryDto deliveryDto);

    @Override
    @PostMapping("/successful")
    void deliverySuccessful(@RequestBody UUID deliveryId);

    @Override
    @PostMapping("/picked")
    void deliveryPicked(@RequestBody UUID deliveryId);

    @Override
    @PostMapping("/failed")
    void deliveryFailed(@RequestBody UUID deliveryId);

    @Override
    @PostMapping("/cost")
    Double deliveryCost(@RequestBody @Valid OrderDto orderDto);
}