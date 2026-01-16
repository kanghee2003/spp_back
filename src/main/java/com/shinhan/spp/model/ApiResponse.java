package com.shinhan.spp.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
@AllArgsConstructor
public class ApiResponse<T> {
    private int code;
    private String message;
    private T item;

    public ApiResponse(final int code, final String message) {
        this.code = code;
        this.message = message;
        this.item = null;
    }

    public static <T> ApiResponse<T> ok() {
        return response(HttpStatus.OK.value(), null, null);
    }

    public static <T> ApiResponse<T> ok(final T item) {
        return response(HttpStatus.OK.value(), null, item);
    }

    public static <T> ApiResponse<T> ok(final T item, final int code) {
        return response(code,null, item);
    }

    public static <T> ApiResponse<T> ok(final T item, final int code, String message) {
        return response(code, message, item);
    }

    private static <T> ApiResponse<T> response(final int code, final String message, final T item) {
        return ApiResponse.<T>builder()
                .code(code)
                .message(message)
                .item(item)
                .build();
    }


}
