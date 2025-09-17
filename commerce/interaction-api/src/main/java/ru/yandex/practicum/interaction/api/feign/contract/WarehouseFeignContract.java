package ru.yandex.practicum.interaction.api.feign.contract;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.interaction.api.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.interaction.api.dto.warehouse.AddProductToWarehouseRequestDto;
import ru.yandex.practicum.interaction.api.dto.warehouse.AddressDto;
import ru.yandex.practicum.interaction.api.dto.warehouse.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.interaction.api.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.interaction.api.dto.warehouse.NewProductInWarehouseRequestDto;
import ru.yandex.practicum.interaction.api.dto.warehouse.ShippedToDeliveryRequest;

import java.util.Map;
import java.util.UUID;

public interface WarehouseFeignContract {

    void newProductInWarehouse(@RequestBody NewProductInWarehouseRequestDto requestDto);

    void shippedToDelivery(@RequestBody ShippedToDeliveryRequest deliveryRequest);

    void acceptReturn(@RequestBody Map<UUID, Long> products);

    BookedProductsDto checkProductQuantityEnoughForShoppingCart(@RequestBody ShoppingCartDto shoppingCartDto);

    BookedProductsDto assemblyProductsForOrder(@RequestBody AssemblyProductsForOrderRequest assemblyProductsForOrder);

    void addProductToWarehouse(@RequestBody AddProductToWarehouseRequestDto requestDto);

    AddressDto getWarehouseAddress();

    BookedProductsDto bookingProducts(@RequestBody ShoppingCartDto shoppingCartDto);
}