package ru.yandex.practicum.commerce.shoppingstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.commerce.shoppingstore.model.Product;

import java.util.UUID;

@Repository
public interface ShoppingStoreRepository extends JpaRepository<Product, UUID> {
}