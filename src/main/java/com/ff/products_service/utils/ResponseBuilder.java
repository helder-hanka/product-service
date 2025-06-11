package com.ff.products_service.utils;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class ResponseBuilder {
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(
                message,
                HttpStatus.OK.value(),
                LocalDateTime.now(),
                data
        );
    }

    public static <T> ApiResponse<T> created(String message, T data) {
        return new ApiResponse<>(
                message,
                HttpStatus.CREATED.value(),
                LocalDateTime.now(),
                data
        );
    }

    public static <T> ApiResponse<T> error(String message, int statusCode) {
        return new ApiResponse<>(
                message,
                statusCode,
                LocalDateTime.now(),
                null
        );
    }
}
