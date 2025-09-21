package ru.yandex.practicum.interaction.api.dto.warehouse;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DimensionDto {
    
    @NotNull(message = "Необходимо указать ширину")
    @Min(value = 1, message = "минимальное значение 1")
    double width;

    @NotNull(message = "Необходимо указать высоту")
    @Min(value = 1, message = "минимальное значение 1")
    double height;

    @NotNull(message = "Необходимо указать глубину")
    @Min(value = 1, message = "минимальное значение 1")
    double depth;
}