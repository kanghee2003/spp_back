package com.shinhan.spp.advice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shinhan.spp.annotation.ResponseDataOnly;
import com.shinhan.spp.model.ApiResponse;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestControllerAdvice
public class ApiResponseAdvice implements ResponseBodyAdvice<Object> {

    private final ObjectMapper objectMapper;

    public ApiResponseAdvice(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(MethodParameter returnType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {

        String path = request.getURI().getPath();
        if (path.startsWith("/v3/api-docs") || path.startsWith("/swagger-ui")) {
            return body;
        }

        // 메서드에 @ResponseDataOnly 있으면 래핑 금지
        if (returnType.hasMethodAnnotation(ResponseDataOnly.class)) {
            return body;
        }

        // 이미 ApiResponse면 그대로 통과
        if (body instanceof ApiResponse) return body;

        // 파일/스트리밍/바이너리 계열은 래핑 제외
        if (body instanceof Resource) return body;
        if (body instanceof StreamingResponseBody) return body;
        if (body instanceof byte[]) return body;

        // 204 No Content 같은 경우는 바디가 없으니 그대로
        Integer status = extractStatus(response);
        if (status != null && status == 204) return body;

        // 2xx가 아니면 래핑하지 않음 (에러는 ExceptionAdvice가 담당)
        if (status != null && (status < 200 || status >= 300)) return body;

        ApiResponse<Object> wrapped = ApiResponse.ok(body);

        // String 리턴 대응 (StringHttpMessageConverter는 문자열만 기대함)
        if (StringHttpMessageConverter.class.isAssignableFrom(selectedConverterType)
                || returnType.getParameterType() == String.class
                || MediaType.TEXT_PLAIN.includes(selectedContentType)) {
            try {
                return objectMapper.writeValueAsString(wrapped);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to serialize ApiResponse", e);
            }
        }

        return wrapped;
    }

    private Integer extractStatus(ServerHttpResponse response) {
        if (response instanceof ServletServerHttpResponse servlet) {
            return servlet.getServletResponse().getStatus();
        }
        return null;
    }
}