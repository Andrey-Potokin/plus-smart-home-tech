package ru.yandex.practicum.commerce.delivery.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.delivery.service.DeliveryService;
import ru.yandex.practicum.interaction.api.dto.delivery.DeliveryDto;
import ru.yandex.practicum.interaction.api.dto.order.OrderDto;
import ru.yandex.practicum.interaction.api.feign.contract.DeliveryFeignContract;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/delivery")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeliveryController  implements DeliveryFeignContract {

    DeliveryService service;

    @Override
    @PutMapping
    public DeliveryDto planDelivery(@RequestBody @Valid DeliveryDto deliveryDto) {
        log.info("Создать новую доставку в БД {}", deliveryDto);
        return service.planDelivery(deliveryDto);
    }

    @Override
    @PostMapping("/successful")
    public void deliverySuccessful(@RequestBody UUID deliveryId) {
        log.info("Эмуляция успешной доставки товара {}", deliveryId);
        service.deliverySuccessful(deliveryId);
    }

    @Override
    @PostMapping("/picked")
    public void deliveryPicked(@RequestBody UUID deliveryId) {
        log.info("Эмуляция получения товара в доставку {}", deliveryId);
        service.deliveryPicked(deliveryId);
    }

    @Override
    @PostMapping("/failed")
    public void deliveryFailed(@RequestBody UUID deliveryId) {
        log.info("Эмуляция неудачного вручения товара {}", deliveryId);
        service.deliveryFailed(deliveryId);
    }

    @Override
    @PostMapping("/cost")
    public Double deliveryCost(@RequestBody @Valid OrderDto orderDto) {
        log.info("Расчёт полной стоимости доставки заказа {}", orderDto);
        return service.deliveryCost(orderDto);
    }
}