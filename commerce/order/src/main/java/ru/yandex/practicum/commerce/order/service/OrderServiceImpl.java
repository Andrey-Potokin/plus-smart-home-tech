package ru.yandex.practicum.commerce.order.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.order.client.DeliveryClient;
import ru.yandex.practicum.commerce.order.client.PaymentClient;
import ru.yandex.practicum.commerce.order.client.ShoppingCartClient;
import ru.yandex.practicum.commerce.order.client.WarehouseClient;
import ru.yandex.practicum.commerce.order.mapper.OrderMapper;
import ru.yandex.practicum.commerce.order.model.Order;
import ru.yandex.practicum.commerce.order.repository.OrderRepository;
import ru.yandex.practicum.interaction.api.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.interaction.api.dto.delivery.DeliveryDto;
import ru.yandex.practicum.interaction.api.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.interaction.api.dto.order.OrderDto;
import ru.yandex.practicum.interaction.api.dto.order.OrderState;
import ru.yandex.practicum.interaction.api.dto.order.ProductReturnRequest;
import ru.yandex.practicum.interaction.api.dto.warehouse.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.interaction.api.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.interaction.api.exception.NoOrderFoundException;
import ru.yandex.practicum.interaction.api.exception.NotAuthorizedUserException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.READ_COMMITTED)
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class OrderServiceImpl implements OrderService {

    OrderMapper mapper;
    OrderRepository repository;
    PaymentClient paymentClient;
    DeliveryClient deliveryClient;
    WarehouseClient warehouseClient;
    ShoppingCartClient shoppingCartClient;

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getClientOrders(String username, Integer page, Integer size) {
        if (StringUtils.isEmpty(username))
            throw new NotAuthorizedUserException("Имя пользователя не должно быть пустым.");
        ShoppingCartDto shoppingCart = shoppingCartClient.getShoppingCart(username);

        Sort sortByCreated = Sort.by(Sort.Direction.DESC, "created");
        PageRequest pageRequest = PageRequest.of(page, size, sortByCreated);
        List<Order> orders = repository.findByShoppingCartId(shoppingCart.getShoppingCartId(), pageRequest);
        return mapper.toOrdersDto(orders);
    }

    @Override
    public OrderDto createNewOrder(CreateNewOrderRequest newOrderRequest) {
        Order order = Order.builder()
                .shoppingCartId(newOrderRequest.getShoppingCart().getShoppingCartId())
                .products(newOrderRequest.getShoppingCart().getProducts())
                .state(OrderState.NEW)
                .build();
        Order newOrder = repository.save(order);

        BookedProductsDto bookedProducts = warehouseClient.assemblyProductsForOrder(
                new AssemblyProductsForOrderRequest(
                        newOrderRequest.getShoppingCart().getShoppingCartId(),
                        newOrder.getOrderId()
                ));

        newOrder.setFragile(bookedProducts.getFragile());
        newOrder.setDeliveryVolume(bookedProducts.getDeliveryVolume());
        newOrder.setDeliveryWeight(bookedProducts.getDeliveryWeight());
        newOrder.setProductPrice(paymentClient.productCost(mapper.toOrderDto(newOrder)));

        DeliveryDto deliveryDto = DeliveryDto.builder()
                .orderId(newOrder.getOrderId())
                .fromAddress(warehouseClient.getWarehouseAddress())
                .toAddress(newOrderRequest.getDeliveryAddress())
                .build();
        newOrder.setDeliveryId(deliveryClient.planDelivery(deliveryDto).getDeliveryId());

        paymentClient.createPayment(mapper.toOrderDto(newOrder));
        return mapper.toOrderDto(newOrder);
    }

    @Override
    public OrderDto productReturn(ProductReturnRequest returnRequest) {
        Order order = repository.findById(returnRequest.getOrderId())
                .orElseThrow(() -> new NoOrderFoundException("Заказ не найден."));
        warehouseClient.acceptReturn(returnRequest.getProducts());
        order.setState(OrderState.PRODUCT_RETURNED);
        return mapper.toOrderDto(order);
    }

    @Override
    public OrderDto payment(UUID orderId) {
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Заказ не найден."));
        order.setState(OrderState.PAID);
        return mapper.toOrderDto(order);
    }

    @Override
    public OrderDto paymentFailed(UUID orderId) {
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Заказ не найден."));
        order.setState(OrderState.PAYMENT_FAILED);
        return mapper.toOrderDto(order);
    }

    @Override
    public OrderDto delivery(UUID orderId) {
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Заказ не найден."));
        order.setState(OrderState.DELIVERED);
        return mapper.toOrderDto(order);
    }

    @Override
    public OrderDto deliveryFailed(UUID orderId) {
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Заказ не найден."));
        order.setState(OrderState.DELIVERY_FAILED);
        return mapper.toOrderDto(order);
    }

    @Override
    public OrderDto complete(UUID orderId) {
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Заказ не найден."));
        order.setState(OrderState.COMPLETED);
        return mapper.toOrderDto(order);
    }

    @Override
    public OrderDto calculateTotalCost(UUID orderId) {
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Заказ не найден."));
        order.setTotalPrice(paymentClient.getTotalCost(mapper.toOrderDto(order)));
        return mapper.toOrderDto(order);
    }

    @Override
    public OrderDto calculateDeliveryCost(UUID orderId) {
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Заказ не найден."));
        order.setDeliveryPrice(deliveryClient.deliveryCost(mapper.toOrderDto(order)));
        return mapper.toOrderDto(order);
    }

    @Override
    public OrderDto assembly(UUID orderId) {
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Заказ не найден."));
        order.setState(OrderState.ASSEMBLED);
        return mapper.toOrderDto(order);
    }

    @Override
    public OrderDto assemblyFailed(UUID orderId) {
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Заказ не найден."));
        order.setState(OrderState.ASSEMBLY_FAILED);
        return mapper.toOrderDto(order);
    }
}