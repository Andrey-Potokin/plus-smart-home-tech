package ru.yandex.practicum.commerce.shoppingstore.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.commerce.shoppingstore.model.Product;
import shoppingstore.ProductDto;

@UtilityClass
public class ProductMapper {

    public static Product toProduct(ProductDto dto) {
        return Product.builder()
                .productName(dto.getProductName())
                .imageSrc(dto.getImageSrc())
                .quantityState(dto.getQuantityState())
                .productState(dto.getProductState())
                .productCategory(dto.getProductCategory())
                .price(dto.getPrice())
                .build();
    }

    public static ProductDto toProductDto(Product product) {
        return ProductDto.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .imageSrc(product.getImageSrc())
                .quantityState(product.getQuantityState())
                .productState(product.getProductState())
                .productCategory(product.getProductCategory())
                .price(product.getPrice())
                .build();
    }
}