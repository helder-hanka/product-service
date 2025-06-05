package com.ff.products_service.utils;

import com.ff.products_service.dto.ProductWithImagesRequest;
import com.ff.products_service.entity.Product;

import java.util.List;
import java.util.stream.Collectors;

public class ProductMapper {
    public static ProductWithImagesRequest toResponse(Product product) {
        ProductWithImagesRequest dto = new ProductWithImagesRequest();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());

        if (product.getImages() != null) {
            List<ProductWithImagesRequest.ImageRequest> imagesDto = product.getImages().stream()
                    .map(image -> {
                        ProductWithImagesRequest.ImageRequest imageDto = new ProductWithImagesRequest.ImageRequest();
                        imageDto.setId(image.getId());
                        imageDto.setUrl(image.getUrl());
                        imageDto.setTitle(image.getTitle());
                        imageDto.setMain(image.isMain());
                        imageDto.setPosition(image.getPosition());
                        return imageDto;
                    })
                    .toList();
            dto.setImages(imagesDto);
        }
        return dto;
    }
}