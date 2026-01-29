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
import java.util.Base64;
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
            UserContext user = om.convertValue(claims, UserContext.class);

            if (user.userId() == null || user.userId().isBlank()) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token: sub missing");
            }

            return user;

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
        if (parts.length < 2) throw new IllegalArgumentException("Invalid JWT: missing parts");

        String payload = parts[1].trim();

        payload = padBase64Url(payload);

        byte[] decoded = Base64.getUrlDecoder().decode(payload);
        String json = new String(decoded, StandardCharsets.UTF_8);

        return om.readValue(json, new TypeReference<Map<String, Object>>() {});
    }

    private static String padBase64Url(String v) {
        int mod = v.length() % 4;
        if (mod == 0) return v;
        return v + "====".substring(mod); // 1->"===", 2->"==", 3->"="
    }
}
