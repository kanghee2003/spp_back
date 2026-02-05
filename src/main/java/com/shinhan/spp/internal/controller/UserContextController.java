package com.shinhan.spp.internal.controller;

import com.shinhan.spp.advice.UserContextCache;
import com.shinhan.spp.internal.dto.UserContextEvictReq;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/internal/user-context")
public class UserContextController {
    
    private final String internalToken;

    public UserContextController(@Value("${app.internal.token:}") String internalToken) {
        this.internalToken = internalToken;
    }

    @PostMapping("/evict")
    public ResponseEntity<?> evict(
            @RequestHeader(value = "X-Internal-Token", required = false) String token,
            @RequestBody(required = false) UserContextEvictReq body
    ) {
        if (internalToken != null && !internalToken.isBlank()) {
            if (token == null || token.isBlank() || !internalToken.equals(token)) {
                return ResponseEntity.status(403).body(Map.of("ok", false, "message", "forbidden"));
            }
        }

        String userId = (body == null) ? null : body.userId();
        if (userId == null || userId.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("ok", false, "message", "userId required"));
        }

        UserContextCache.evict(userId);
        return ResponseEntity.ok(Map.of(
                "ok", true,
                "userId", userId,
                "eventId", body.eventId()
        ));
    }
}
