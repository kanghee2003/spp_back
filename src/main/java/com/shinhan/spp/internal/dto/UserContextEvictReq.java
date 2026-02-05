package com.shinhan.spp.internal.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "서버 간 UserContext 캐시 무효화 요청 바디.")
public record UserContextEvictReq(
        String userId,
        String eventId
) {}
