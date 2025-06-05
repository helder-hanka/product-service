package com.ff.products_service.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductWithImagesRequest {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private int stock;
    private List<ImageRequest> images;

    @Data
    public static class ImageRequest {
        private Long id;
        private String url;
        private String title;
        private boolean isMain;
        private int position;
    }
}
