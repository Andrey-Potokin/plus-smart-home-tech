package ru.yandex.practicum.interaction.api.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.interaction.api.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.interaction.api.dto.warehouse.AddressDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateNewOrderRequest {

    @NotNull(message = "Корзина не может быть пустой")
    ShoppingCartDto shoppingCart;

    @NotNull(message = "Адрес доставки не может быть пустым")
    AddressDto deliveryAddress;
}