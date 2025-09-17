package ru.yandex.practicum.shopping.store.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.interaction.api.dto.store.ProductCategory;
import ru.yandex.practicum.shopping.store.model.Product;

import java.util.UUID;

public interface ShoppingStoreRepository extends JpaRepository<Product, UUID> {
    Page<Product> findAllByProductCategory(ProductCategory productCategory, Pageable pageable);
}
