package ru.yandex.practicum.commerce.payment.client;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.interaction.api.dto.store.ProductCategory;
import ru.yandex.practicum.interaction.api.dto.store.ProductDto;
import ru.yandex.practicum.interaction.api.dto.store.QuantityState;
import ru.yandex.practicum.interaction.api.feign.contract.ShoppingStoreFeignContract;

import java.util.UUID;

@FeignClient(name = "shopping-store", path = "/api/v1/shopping-store")
public interface ShoppingStoreClient extends ShoppingStoreFeignContract {

    @Override
    @GetMapping
    Page<ProductDto> getProducts(@RequestParam ProductCategory productCategory, @Valid Pageable pageable);

    @Override
    @PutMapping
    ProductDto createNewProduct(@RequestBody ProductDto productDto);

    @Override
    @PostMapping
    ProductDto updateProduct(@RequestBody ProductDto productDto);

    @Override
    @PostMapping("/removeProductFromStore")
    Boolean removeProductFromStore(@RequestBody UUID productId);

    @Override
    @PostMapping("/quantityState")
    Boolean setProductQuantityState(@RequestParam UUID productId, @RequestParam QuantityState quantityState);

    @Override
    @GetMapping("/{productId}")
    ProductDto getProduct(@PathVariable UUID productId);
}