package com.shinhan.spp.api;

import com.shinhan.spp.dto.MockAutoCompleteOrgOutDto;
import com.shinhan.spp.dto.MockAutoCompleteUserOutDto;
import com.shinhan.spp.dto.out.MockAutoCompletePageDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * AutoComplete 무한스크롤용 Mock API
 *
 * 프론트는 useInfiniteQuery로 cursor(=page) 기반으로 호출하며,
 *    백엔드는 실제 DB 대신 mock 데이터를 만들어 반환합니다.
 *
 * 응답은 ApiResponseAdvice에 의해 자동으로 (code/message/item) 형태로 래핑됩니다.
 */
@Tag(name = "MockAutoCompleteController", description = "AutoComplete 무한스크롤 Mock API")
@RestController
@RequestMapping("/api/auto-complete")
public class MockAutoCompleteController {

    @Operation(summary = "user")
    @GetMapping("/user")
    public MockAutoCompletePageDto<MockAutoCompleteUserOutDto> autoCompleteUser(
            @RequestParam(name = "q", required = false, defaultValue = "") String q,
            @RequestParam(name = "cursor", required = false, defaultValue = "1") Integer cursor,
            @RequestParam(name = "size", required = false, defaultValue = "20") Integer size
    ) {
        // 방어
        int safeCursor = (cursor == null || cursor < 1) ? 1 : cursor;
        int safeSize = (size == null || size < 1) ? 20 : Math.min(size, 100);

        final int total = 93; // demo total
        int start = (safeCursor - 1) * safeSize;
        int end = Math.min(start + safeSize, total);

        List<MockAutoCompleteUserOutDto> items = new ArrayList<>();
        if (start < total) {
            for (int i = start; i < end; i++) {
                int idx = i + 1;
                items.add(MockAutoCompleteUserOutDto.builder()
                        .value((q == null || q.isBlank() ? "item" : q) + "-" + idx)
                        .userId("001" + String.valueOf(i))
                        .gradeCd("G" + ((idx % 5) + 1))
                        .gradeNm("Grade" + ((idx % 5) + 1))
                        .orgCd("ORG" + ((idx % 7) + 1))
                        .orgNm("Org" + ((idx % 7) + 1))
                        .telNo("010-0000-" + String.format("%04d", idx))
                        .build());
            }
        }

        boolean hasMore = end < total;
        Integer nextCursor = hasMore ? safeCursor + 1 : null;

        return MockAutoCompletePageDto.<MockAutoCompleteUserOutDto>builder()
                .items(items)
                .hasMore(hasMore)
                .nextCursor(nextCursor)
                .build();
    }


    @Operation(summary = "org")
    @GetMapping("/org")
    public MockAutoCompletePageDto<MockAutoCompleteOrgOutDto> autoCompleteOrg(
            @RequestParam(name = "q", required = false, defaultValue = "") String q,
            @RequestParam(name = "cursor", required = false, defaultValue = "1") Integer cursor,
            @RequestParam(name = "size", required = false, defaultValue = "20") Integer size
    ) {
        // 방어
        int safeCursor = (cursor == null || cursor < 1) ? 1 : cursor;
        int safeSize = (size == null || size < 1) ? 20 : Math.min(size, 100);

        final int total = 93; // demo total
        int start = (safeCursor - 1) * safeSize;
        int end = Math.min(start + safeSize, total);

        List<MockAutoCompleteOrgOutDto> items = new ArrayList<>();
        if (start < total) {
            for (int i = start; i < end; i++) {
                int idx = i + 1;
                items.add(MockAutoCompleteOrgOutDto.builder()
                        .value((q == null || q.isBlank() ? "org" : q) + "-" + idx)
                        .orgCd("ORG" + String.format("%03d", idx))
                        .orgNm("Org" + idx)
                        .build());
            }
        }

        boolean hasMore = end < total;
        Integer nextCursor = hasMore ? safeCursor + 1 : null;

        return MockAutoCompletePageDto.<MockAutoCompleteOrgOutDto>builder()
                .items(items)
                .hasMore(hasMore)
                .nextCursor(nextCursor)
                .build();
    }
}
