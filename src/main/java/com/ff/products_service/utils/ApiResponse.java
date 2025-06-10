package com.ff.products_service.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ApiResponse<T> {
    private String message;
    private int status;
    private LocalDateTime timestamp;
    private T data;
}
