package com.ff.products_service.dto;

import com.ff.products_service.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductSaveResponseDTO {
    private String message;
    private ProductResponseDTO data;
}