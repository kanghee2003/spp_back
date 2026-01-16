package com.shinhan.spp.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.regex.Pattern;

public class CsrfRequireMatcher implements RequestMatcher {

    private static final Pattern ALLOWED_METHODS =
            Pattern.compile("^(GET|HEAD|TRACE|OPTIONS)$");

    @Override
    public boolean matches(HttpServletRequest request) {
        // 안전 메서드는 CSRF 보호 대상에서 제외
        if (ALLOWED_METHODS.matcher(request.getMethod()).matches()) {
            return false;
        }

        // Swagger UI에서 발생하는 요청은 CSRF 대상에서 제외
        String referer = request.getHeader(HttpHeaders.REFERER);
        return referer == null || !referer.contains("/swagger-ui");
    }
}