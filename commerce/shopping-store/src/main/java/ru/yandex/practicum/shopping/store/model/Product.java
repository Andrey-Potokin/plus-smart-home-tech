package ru.yandex.practicum.shopping.store.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UuidGenerator;
import ru.yandex.practicum.interaction.api.dto.store.ProductCategory;
import ru.yandex.practicum.interaction.api.dto.store.ProductState;
import ru.yandex.practicum.interaction.api.dto.store.QuantityState;

import java.util.UUID;

@Entity
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product {

    @Id @UuidGenerator
    UUID productId;

    @NotBlank(message = "Наименование товара не может быть пустым")
    @Column(nullable = false)
    String productName;

    @NotBlank(message = "Описание товара не может быть пустым")
    @Column(nullable = false)
    String description;

    String imageSrc;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @NotNull(message = "Необходимо указать остаток товара")
    QuantityState quantityState;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @NotNull(message = "Необходимо указать статус товара")
    ProductState productState;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @NotNull(message = "Необходимо указать категорию товара")
    ProductCategory productCategory;

    @NotNull(message = "Необходимо указать цену товара")
    double price;
}