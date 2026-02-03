package com.shinhan.spp.dto.cm.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * AutoComplete 무한스크롤(Mock) 페이지 응답 DTO
 *
 * ApiResponseAdvice가 (code/message/item)으로 자동 래핑하므로,
 * item 내부는 프론트의 zod 스키마(strict)와 1:1로 맞춰야 합니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MockAutoCompletePageDto<T> {
    private List<T> items;
    private Integer nextCursor; // 다음 페이지 커서 (없으면 null)
    private boolean hasMore;
}
