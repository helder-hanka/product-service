package com.ff.products_service.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProductWithImagesRequest {
    private String name;
    private String description;
    private String price;
    private int stock;
    private List<String> images;

    @Data
    public static class ImageRequest {
        private String url;
        private String title;
        private String isMain;
    }
}
