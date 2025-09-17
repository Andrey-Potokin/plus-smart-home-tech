package ru.yandex.practicum.shopping.cart.client;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

@FeignClient(name = "warehouse", path = "/api/v1/warehouse")
public interface WarehouseClient extends WarehouseFeignContract {

    @Override
    @PutMapping
    void newProductInWarehouse(@RequestBody @Valid NewProductInWarehouseRequestDto requestDto);

    @Override
    @PostMapping("/shipped")
    void shippedToDelivery(@RequestBody ShippedToDeliveryRequest deliveryRequest);

    @Override
    @PostMapping("/return")
    void acceptReturn(@RequestBody Map<UUID, Long> products);

    @Override
    @PostMapping("/check")
    BookedProductsDto checkProductQuantityEnoughForShoppingCart(@RequestBody @Valid ShoppingCartDto shoppingCartDto);

    @Override
    @PostMapping("/assembly")
    BookedProductsDto assemblyProductsForOrder(@RequestBody @Valid AssemblyProductsForOrderRequest assemblyProductsForOrder);

    @Override
    @PostMapping("/add")
    void addProductToWarehouse(@RequestBody @Valid AddProductToWarehouseRequestDto requestDto);

    @Override
    @GetMapping("/address")
    AddressDto getWarehouseAddress();

    @Override
    @PostMapping("/booking")
    BookedProductsDto bookingProducts(@RequestBody @Valid ShoppingCartDto shoppingCartDto);
}