package com.ff.products_service.dto;

import lombok.Data;

@Data
public class ImageResponseDTO {
    private Long id;
    private String url;
    private String title;
    private boolean main;
}
