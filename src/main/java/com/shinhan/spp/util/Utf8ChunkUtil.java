package com.shinhan.spp.util;

import com.shinhan.spp.exception.custom.BusinessException;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class Utf8ChunkUtil {
    public static List<String> splitByUtf8Bytes(String s, int maxBytes) {
        List<String> parts = new ArrayList<>();
        if (s == null || s.isEmpty())
            return parts;
        if (maxBytes <= 0)
            throw new BusinessException("maxBytes는 0보다 커야 합니다");

        int i = 0;
        final int n = s.length();

        while (i < n) {
            int low = i + 1;
            int high = n;
            int target = i;

            // substring(i, mid)의 UTF-8 byte 길이가 maxBytes 이하인 최대 mid 찾기
            while (low <= high) {
                int mid = (low + high) >>> 1;
                String sub = s.substring(i, mid);
                int bytes = sub.getBytes(StandardCharsets.UTF_8).length;

                if (bytes <= maxBytes) {
                    target = mid;
                    low = mid + 1;
                } else {
                    high = mid - 1;
                }
            }

            if (target == i) target = i + 1;

            parts.add(s.substring(i, target));
            i = target;
        }

        return parts;
    }

    public static int utf8Bytes(String s) {
        return s == null ? 0 : s.getBytes(StandardCharsets.UTF_8).length;
    }
}