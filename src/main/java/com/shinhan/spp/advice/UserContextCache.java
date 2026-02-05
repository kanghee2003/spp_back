package com.shinhan.spp.advice;

import com.shinhan.spp.model.UserContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class UserContextCache {

    private UserContextCache() {}
    private static final int MAX_ENTRIES = 20_000;

    private static final Map<String, UserContext> CACHE = new ConcurrentHashMap<>();

    public static UserContext get(String userId) {
        if (userId == null) return null;
        return CACHE.get(userId);
    }

    public static void put(String userId, UserContext ctx) {
        if (userId == null || userId.isBlank() || ctx == null) return;
        if (CACHE.size() > MAX_ENTRIES) {
            CACHE.clear();
        }
        CACHE.put(userId, ctx);
    }

    public static void evict(String userId) {
        if (userId == null || userId.isBlank()) return;
        CACHE.remove(userId);
    }
}
