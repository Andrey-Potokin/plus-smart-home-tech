package ru.yandex.practicum.commerce.warehouse.model;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "warehouse_product")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Warehouse {

    @Id
    UUID productId;
    Long quantity;
    boolean fragile;

    @Embedded
    Dimension dimension;
    
    double weight;
}