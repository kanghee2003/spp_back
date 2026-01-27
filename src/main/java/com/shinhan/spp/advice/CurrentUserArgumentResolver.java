package com.shinhan.spp.advice;

import com.shinhan.spp.annotation.CurrentUser;
import com.shinhan.spp.model.UserContext;
import com.shinhan.spp.provider.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtTokenProvider jwt;

    public CurrentUserArgumentResolver(JwtTokenProvider jwt) {
        this.jwt = jwt;
    }

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
        String token = JwtTokenProvider.resolveBearer(authHeader);

        if (token == null) {
            if (!required) return null;
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing Bearer token");
        }

        try {
            Claims claims = jwt.parseClaims(token);

            // 표준: sub = userId
            String userId = claims.getSubject();
            if (userId == null || userId.isBlank()) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token: subject missing");
            }

            // 너네 토큰 claim 키에 맞춰서 조정
            String orgCd = claims.get("orgCd", String.class);
            String userNm = claims.get("userNm", String.class);

            // roles 키도 너네 토큰에 맞춰 조정 ("roles" / "authorities" 등)
            List<String> roles = readRoles(claims, "roles");

            return new UserContext(userId, orgCd, userNm, roles);

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            // 만료/서명오류 등 전부 401 처리
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token", e);
        }
    }

    @SuppressWarnings("unchecked")
    private static List<String> readRoles(Claims claims, String key) {
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
