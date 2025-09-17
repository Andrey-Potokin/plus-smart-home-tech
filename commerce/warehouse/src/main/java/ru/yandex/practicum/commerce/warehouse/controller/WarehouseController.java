package ru.yandex.practicum.commerce.warehouse.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.warehouse.service.WarehouseService;
import ru.yandex.practicum.interaction.api.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.interaction.api.dto.warehouse.AddProductToWarehouseRequestDto;
import ru.yandex.practicum.interaction.api.dto.warehouse.AddressDto;
import ru.yandex.practicum.interaction.api.dto.warehouse.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.interaction.api.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.interaction.api.dto.warehouse.NewProductInWarehouseRequestDto;
import ru.yandex.practicum.interaction.api.dto.warehouse.ShippedToDeliveryRequest;
import ru.yandex.practicum.interaction.api.feign.contract.WarehouseFeignContract;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/warehouse")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WarehouseController implements WarehouseFeignContract {

    WarehouseService warehouseService;

    @Override
    @PutMapping
    public void newProductInWarehouse(@RequestBody @Valid NewProductInWarehouseRequestDto requestDto) {
        log.info("Добавить новый товар на склад {}", requestDto);
        warehouseService.newProductInWarehouse(requestDto);
    }

    @Override
    @PostMapping("/shipped")
    public void shippedToDelivery(ShippedToDeliveryRequest deliveryRequest) {
        log.info("Передать товары в доставку {}", deliveryRequest);
        warehouseService.shippedToDelivery(deliveryRequest);
    }

    @Override
    @PostMapping("/return")
    public void acceptReturn(@RequestBody Map<UUID, Long> products) {
        log.info("Принять возврат товаров на склад {}", products);
        warehouseService.acceptReturn(products);
    }

    @Override
    @PostMapping("/check")
    public BookedProductsDto checkProductQuantityEnoughForShoppingCart(@RequestBody @Valid
                                                                       ShoppingCartDto shoppingCartDto) {
        log.info("Предварительно проверить что количество товаров на складе достаточно для данной корзины продуктов {}",
                shoppingCartDto);
        return warehouseService.checkProductQuantityEnoughForShoppingCart(shoppingCartDto);
    }

    @Override
    @PostMapping("/assembly")
    public BookedProductsDto assemblyProductsForOrder(@RequestBody @Valid
                                                          AssemblyProductsForOrderRequest assemblyProductsForOrder) {
        log.info("Собрать товары к заказу для подготовки к отправке {}", assemblyProductsForOrder);
        return warehouseService.assemblyProductsForOrder(assemblyProductsForOrder);
    }

    @Override
    @PostMapping("/add")
    public void addProductToWarehouse(@RequestBody @Valid AddProductToWarehouseRequestDto requestDto) {
        log.info("Принять товар на склад {}", requestDto);
        warehouseService.addProductToWarehouse(requestDto);
    }

    @Override
    @GetMapping("/address")
    public AddressDto getWarehouseAddress() {
        log.info("Предоставить адрес склада для расчёта доставки.");
        return warehouseService.getWarehouseAddress();
    }

    @Override
    @PostMapping("/booking")
    public BookedProductsDto bookingProducts(@RequestBody @Valid ShoppingCartDto shoppingCartDto) {
        log.info("Бронирование корзины покупок {}", shoppingCartDto);
        return warehouseService.bookingProducts(shoppingCartDto);
    }
}