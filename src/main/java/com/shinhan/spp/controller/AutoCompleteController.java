package com.shinhan.spp.controller;

import com.shinhan.spp.dto.AutoCompleteOrgOutDto;
import com.shinhan.spp.dto.AutoCompleteUserOutDto;
import com.shinhan.spp.dto.in.AutoCompleteSearchInDto;
import com.shinhan.spp.dto.out.MockAutoCompletePageDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


@Tag(name = "AutoCompleteController", description = "AutoComplete API")
@RestController
@RequestMapping("/api/auto-complete")
public class AutoCompleteController {

    @Operation(summary = "user")
    @GetMapping("/user")
    public MockAutoCompletePageDto<AutoCompleteUserOutDto> autoCompleteUser(AutoCompleteSearchInDto autoCompleteSearchInDto) {
        // 방어
        int safeCursor = (autoCompleteSearchInDto.getCursor() == null || autoCompleteSearchInDto.getCursor() < 1) ? 1 : autoCompleteSearchInDto.getCursor();
        int safeSize = (autoCompleteSearchInDto.getSize() == null || autoCompleteSearchInDto.getSize() < 1) ? 20 : Math.min(autoCompleteSearchInDto.getSize(), 100);

        final int total = 93; // demo total
        int start = (safeCursor - 1) * safeSize;
        int end = Math.min(start + safeSize, total);

        List<AutoCompleteUserOutDto> items = new ArrayList<>();
        if (start < total) {
            for (int i = start; i < end; i++) {
                int idx = i + 1;
                items.add(AutoCompleteUserOutDto.builder()
                        .value((autoCompleteSearchInDto.getQuery() == null || autoCompleteSearchInDto.getQuery().isBlank() ? "item" : autoCompleteSearchInDto.getQuery()) + "-" + idx)
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

        return MockAutoCompletePageDto.<AutoCompleteUserOutDto>builder()
                .items(items)
                .hasMore(hasMore)
                .nextCursor(nextCursor)
                .build();
    }


    @Operation(summary = "org")
    @GetMapping("/org")
    public MockAutoCompletePageDto<AutoCompleteOrgOutDto> autoCompleteOrg(AutoCompleteSearchInDto autoCompleteSearchInDto) {
        // 방어
        int safeCursor = (autoCompleteSearchInDto.getCursor() == null || autoCompleteSearchInDto.getCursor() < 1) ? 1 : autoCompleteSearchInDto.getCursor();
        int safeSize = (autoCompleteSearchInDto.getSize() == null || autoCompleteSearchInDto.getSize() < 1) ? 20 : Math.min(autoCompleteSearchInDto.getSize(), 100);

        final int total = 93; // demo total
        int start = (safeCursor - 1) * safeSize;
        int end = Math.min(start + safeSize, total);

        List<AutoCompleteOrgOutDto> items = new ArrayList<>();
        if (start < total) {
            for (int i = start; i < end; i++) {
                int idx = i + 1;
                items.add(AutoCompleteOrgOutDto.builder()
                        .value((autoCompleteSearchInDto.getQuery() == null || autoCompleteSearchInDto.getQuery().isBlank() ? "org" : autoCompleteSearchInDto.getQuery()) + "-" + idx)
                        .orgCd("ORG" + String.format("%03d", idx))
                        .orgNm("Org" + idx)
                        .build());
            }
        }

        boolean hasMore = end < total;
        Integer nextCursor = hasMore ? safeCursor + 1 : null;

        return MockAutoCompletePageDto.<AutoCompleteOrgOutDto>builder()
                .items(items)
                .hasMore(hasMore)
                .nextCursor(nextCursor)
                .build();
    }
}
