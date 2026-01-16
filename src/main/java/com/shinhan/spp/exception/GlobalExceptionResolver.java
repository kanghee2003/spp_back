package com.shinhan.spp.exception;

import com.shinhan.spp.enums.ErrorCode;
import com.shinhan.spp.exception.custom.BusinessException;
import com.shinhan.spp.model.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.ResponseEntity;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionResolver {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        ErrorCode ec = e.getErrorCode();
        String msg = (e.getMessage() == null || e.getMessage().isBlank())
                ? ec.getMessage()
                : e.getMessage();

        log.warn("BusinessException(code={}): {}", ec.getCode(), e.getMessage(), e);
        return ResponseEntity
                .status(ec.getCode())
                .body(ApiResponse.ok(null, ec.getCode(), msg));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ErrorCode ec = ErrorCode.INVALID_PARAMETER_ERROR;

        String msg = ec.getMessage();
        FieldError fe = e.getBindingResult().getFieldError();
        if (fe != null) {
            msg = fe.getField() + ": " + fe.getDefaultMessage();
        }

        log.warn("ValidationError: {}", msg);
        return ResponseEntity
                .status(ec.getCode())
                .body(ApiResponse.ok(null, ec.getCode(), msg));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        ErrorCode ec = ErrorCode.INVALID_PARAMETER_ERROR;
        String msg = "필수 파라미터가 누락되었습니다: " + e.getParameterName();

        log.warn("MissingParameter: {}", msg);
        return ResponseEntity
                .status(ec.getCode())
                .body(ApiResponse.ok(null, ec.getCode(), msg));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        ErrorCode ec = ErrorCode.INVALID_PARAMETER_ERROR;
        String msg = "요청 본문(JSON) 형식이 올바르지 않습니다.";

        log.warn("HttpMessageNotReadable: {}", e.getMessage());
        return ResponseEntity
                .status(ec.getCode())
                .body(ApiResponse.ok(null, ec.getCode(), msg));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        ErrorCode ec = ErrorCode.INTERNAL_SERVER_ERROR;
        log.error("Unhandled exception", e);
        return ResponseEntity
                .status(ec.getCode())
                .body(ApiResponse.ok(null, ec.getCode(), ec.getMessage()));
    }
}