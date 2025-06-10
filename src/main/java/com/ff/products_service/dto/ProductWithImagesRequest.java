package com.ff.products_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductWithImagesRequest {
    @NotBlank
    @NotBlank(message = "Le Nom est obligatoire")
    private String name;
    @NotBlank(message = "La déscription est obligatoire")
    private String description;
    @NotNull(message = "Le prix est obligatoire")
    private BigDecimal price;
    @NotNull(message = "Le stock est obligatoire")
    private int stock;
    @Valid
    private List<ImageRequest> images;

    @Data
    public static class ImageRequest {
        @NotBlank
        private String url;
        @NotBlank
        private String title;
        @NotNull
        private boolean isMain;
        @NotNull
        private boolean toDelete;
    }
}
