package com.shinhan.spp.advice;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shinhan.spp.annotation.CurrentUser;
import com.shinhan.spp.model.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    private static final ObjectMapper om = new ObjectMapper();

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class)
                && UserContext.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        CurrentUser anno = parameter.getParameterAnnotation(CurrentUser.class);
        boolean required = (anno == null) || anno.required();

        HttpServletRequest req = webRequest.getNativeRequest(HttpServletRequest.class);
        String authHeader = (req == null) ? null : req.getHeader("Authorization");
        String token = resolveBearer(authHeader);

        if (token == null) {
            if (!required) return null;
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing Bearer token");
        }

        try {
            Map<String, Object> claims = decodeJwtPayload(token);

            // 표준: sub = userId
            String userId = getString(claims, "sub");
            if (userId == null || userId.isBlank()) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token: subject missing");
            }

            // 너네 토큰 claim 키에 맞춰서 조정
            String orgCd = getString(claims, "orgCd");
            String userNm = getString(claims, "userNm");

            // roles 키도 너네 토큰에 맞춰 조정 ("roles" / "authorities" 등)
            List<String> roles = readRoles(claims, "roles");

            return new UserContext(userId, orgCd, userNm, roles);

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token", e);
        }
    }

    private static String resolveBearer(String authHeader) {
        if (authHeader == null) return null;
        if (!authHeader.startsWith("Bearer ")) return null;
        return authHeader.substring(7).trim();
    }

    private static Map<String, Object> decodeJwtPayload(String token) throws Exception {
        String[] parts = token.split("\\.");
        if (parts.length < 2) throw new IllegalArgumentException("Invalid JWT");

        String payload = parts[1];
        byte[] decoded = Base64.getUrlDecoder().decode(payload);
        String json = new String(decoded, StandardCharsets.UTF_8);

        return om.readValue(json, new TypeReference<Map<String, Object>>() {});
    }

    private static String getString(Map<String, Object> claims, String key) {
        Object v = claims.get(key);
        return v == null ? null : v.toString();
    }

    @SuppressWarnings("unchecked")
    private static List<String> readRoles(Map<String, Object> claims, String key) {
        Object raw = claims.get(key);
        if (raw == null) return Collections.emptyList();

        // 1) JSON 배열: ["ROLE_USER","ROLE_ADMIN"]
        if (raw instanceof List<?> list) {
            List<String> out = new ArrayList<>();
            for (Object o : list) {
                if (o != null) out.add(o.toString());
            }
            return out;
        }

        // 2) 콤마 문자열: "ROLE_USER,ROLE_ADMIN"
        String s = raw.toString().trim();
        if (s.isEmpty()) return Collections.emptyList();

        if (s.contains(",")) {
            String[] arr = s.split(",");
            List<String> out = new ArrayList<>();
            for (String r : arr) {
                String v = r.trim();
                if (!v.isEmpty()) out.add(v);
            }
            return out;
        }

        // 3) 단일 문자열: "ROLE_USER"
        return List.of(s);
    }
}
