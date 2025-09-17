package ru.yandex.practicum.interaction.api.feign.contract;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.interaction.api.dto.store.ProductCategory;
import ru.yandex.practicum.interaction.api.dto.store.ProductDto;
import ru.yandex.practicum.interaction.api.dto.store.QuantityState;

import java.util.UUID;

public interface ShoppingStoreFeignContract {

    Page<ProductDto> getProducts(@RequestParam ProductCategory category, Pageable pageable);

    ProductDto createNewProduct(@RequestBody ProductDto newProductDto);

    ProductDto updateProduct(@RequestBody ProductDto updateProductDto);

    Boolean removeProductFromStore(@RequestBody UUID productId);

    Boolean setProductQuantityState(@RequestParam UUID productId,
                                @RequestParam QuantityState quantityState);

    ProductDto getProduct(@PathVariable UUID productId);
}