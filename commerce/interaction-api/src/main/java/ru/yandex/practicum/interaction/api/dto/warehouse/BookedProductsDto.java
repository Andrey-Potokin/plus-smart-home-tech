package ru.yandex.practicum.interaction.api.dto.warehouse;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookedProductsDto {
    
    @NotNull(message = "Общий вес доставки обязателен")
    double deliveryWeight;

    @NotNull(message = "Общие объём доставки обязателен")
    double deliveryVolume;

    @NotNull(message = "Наличие хрупких вещей в доставке обязательно к указанию")
    Boolean fragile;
}