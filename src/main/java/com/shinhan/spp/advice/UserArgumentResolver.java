package com.shinhan.spp.advice;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shinhan.spp.annotation.UserInfo;
import com.shinhan.spp.model.UserContext;
import com.shinhan.spp.service.SampleService;
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

public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    private static final ObjectMapper om = new ObjectMapper();

    private final SampleService sampleService;

    public UserArgumentResolver(SampleService sampleService) {
        this.sampleService = sampleService;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(UserInfo.class)
                && UserContext.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        UserInfo anno = parameter.getParameterAnnotation(UserInfo.class);
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
            UserContext base = om.convertValue(claims, UserContext.class);

            if (base.userId() == null || base.userId().isBlank()) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token: sub missing");
            }

            // 1) 캐시에 정보가 존재하면 DB 조회 없이 그대로 사용
            UserContext cached = UserContextCache.get(base.userId());
            if (cached != null) {
                return cached;
            }

            // 2) 캐시에 정보가 없으면 DB 조회해서 UserContext에 저장
            com.shinhan.spp.domain.UserInfo info = null;
            try {
                info = sampleService.selectUserInfo(base.userId());
            } catch (Exception ignore) {
                // 사용자 컨텍스트 조회 실패 시 토큰 기반 정보로만 진행
            }

            UserContext enriched = (info == null) ? base : mergeUserInfo(base, info);
            UserContextCache.put(base.userId(), enriched);
            return enriched;

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

    private static UserContext mergeUserInfo(UserContext base, com.shinhan.spp.domain.UserInfo info) {
        return new UserContext(
                base.userId(),
                base.profileUrl(),
                base.roles(),
                base.departments(),
                base.otherWorkers(),
                pick(base.personalPhone(), info.getHpNo()),
                base.companyName(),
                base.memo(),
                pick(base.employeeNo(), info.getUserCd()),
                pick(base.employeeName(), info.getUserName()),
                base.positionName(),
                base.workLocation(),
                base.unitType(),
                pick(base.webEmail(), info.getEmail()),
                base.companyNo(),
                base.companyEmail(),
                pick(base.companyPhone(), info.getTelNo()),
                base.profileImageUrl(),
                base.parentGwCmpCd(),
                pick(base.departmentName(), info.getDeptCd()),
                base.chargeWork(),
                base.innerLinePhone(),
                base.parentCompanyCode(),
                base.absenteeismInfo(),
                base.faxNumber(),
                base.iat(),
                base.exp()
        );
    }

    private static String pick(String current, String fallback) {
        if (current != null && !current.isBlank()) return current;
        if (fallback != null && !fallback.isBlank()) return fallback;
        return current;
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
