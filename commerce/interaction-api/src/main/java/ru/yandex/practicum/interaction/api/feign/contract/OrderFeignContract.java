package ru.yandex.practicum.interaction.api.feign.contract;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.interaction.api.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.interaction.api.dto.order.OrderDto;
import ru.yandex.practicum.interaction.api.dto.order.ProductReturnRequest;

import java.util.List;
import java.util.UUID;

public interface OrderFeignContract {

    List<OrderDto> getClientOrders(@RequestParam String username,
                                   @RequestParam(defaultValue = "0") Integer page,
                                   @RequestParam(defaultValue = "10") Integer size);

    OrderDto createNewOrder(@RequestBody CreateNewOrderRequest newOrderRequest);

    OrderDto productReturn(@RequestBody ProductReturnRequest returnRequest);

    OrderDto payment(@RequestBody UUID orderId);

    OrderDto paymentFailed(@RequestBody UUID orderId);

    OrderDto delivery(@RequestBody UUID orderId);

    OrderDto deliveryFailed(@RequestBody UUID orderId);

    OrderDto complete(@RequestBody UUID orderId);

    OrderDto calculateTotalCost(@RequestBody UUID orderId);

    OrderDto calculateDeliveryCost(@RequestBody UUID orderId);

    OrderDto assembly(@RequestBody UUID orderId);

    OrderDto assemblyFailed(@RequestBody UUID orderId);
}