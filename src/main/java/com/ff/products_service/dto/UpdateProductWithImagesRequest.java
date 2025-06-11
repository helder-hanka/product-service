package com.ff.products_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

@Data
public class UpdateProductWithImagesRequest {
    @NotNull(message = "Le Id Produit est obligatoire")
    private Long id;
    @NotBlank(message = "Le Nom est obligatoire")
    private String name;
    @NotBlank(message = "La déscription est obligatoire")
    private String description;
    @NotNull(message = "Le prix est obligatoire")
    private BigDecimal price;
    @NotNull(message = "Le stock est obligatoire")
    private int stock;
    @Valid
    @NotEmpty(message = "La liste d'images ne peut être vide")
    private List<UpdateProductWithImagesRequest.ImageRequest> images;

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class ImageRequest extends AbstractImageRequest {
        private Long id;
        @NotBlank(message = "L'URL est obligatoire")
        private String url;
        @NotBlank(message = "Le Titre est obligatoire")
        private String title;
        @NotNull(message = "Le Main est obligatoire")
        private Boolean main;
        @NotNull(message = "Le boolean delete est obligatoire")
        private Boolean toDelete;
    }
}