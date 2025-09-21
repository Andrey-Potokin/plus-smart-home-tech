package ru.yandex.practicum.commerce.delivery.client;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.interaction.api.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.interaction.api.dto.order.OrderDto;
import ru.yandex.practicum.interaction.api.dto.order.ProductReturnRequest;
import ru.yandex.practicum.interaction.api.feign.contract.OrderFeignContract;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "order", path = "/api/v1/order")
public interface OrderClient extends OrderFeignContract {

    @Override
    @GetMapping
    List<OrderDto> getClientOrders(@RequestParam String username,
                                   @RequestParam(defaultValue = "0") Integer page,
                                   @RequestParam(defaultValue = "10") Integer size);

    @Override
    @PutMapping
    OrderDto createNewOrder(@RequestBody @Valid CreateNewOrderRequest newOrderRequest);

    @Override
    @PostMapping("/return")
    OrderDto productReturn(@RequestBody @Valid ProductReturnRequest returnRequest);

    @Override
    @PostMapping("/payment")
    OrderDto payment(@RequestBody UUID orderId);

    @Override
    @PostMapping("/payment/failed")
    OrderDto paymentFailed(@RequestBody UUID orderId);

    @Override
    @PostMapping("/delivery")
    OrderDto delivery(@RequestBody UUID orderId);

    @Override
    @PostMapping("/delivery/failed")
    OrderDto deliveryFailed(@RequestBody UUID orderId);

    @Override
    @PostMapping("/completed")
    OrderDto complete(@RequestBody UUID orderId);

    @Override
    @PostMapping("/calculate/total")
    OrderDto calculateTotalCost(@RequestBody UUID orderId);

    @Override
    @PostMapping("/calculate/delivery")
    OrderDto calculateDeliveryCost(@RequestBody UUID orderId);

    @Override
    @PostMapping("/assembly")
    OrderDto assembly(@RequestBody UUID orderId);

    @Override
    @PostMapping("/assembly/failed")
    OrderDto assemblyFailed(@RequestBody UUID orderId);
}