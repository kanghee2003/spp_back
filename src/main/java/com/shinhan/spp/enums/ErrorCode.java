package com.shinhan.spp.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    INVALID_PARAMETER_ERROR(HttpStatus.BAD_REQUEST.value(), "요청 값이 올바르지 않습니다."),
    BUSINESS_ERROR(HttpStatus.BAD_REQUEST.value(), "업무 처리 중 오류가 발생했습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "실행중 오류가 발생했습니다.");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}