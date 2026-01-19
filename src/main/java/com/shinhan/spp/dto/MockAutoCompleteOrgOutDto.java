package com.shinhan.spp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AutoComplete 무한스크롤(Mock) 조직 아이템 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MockAutoCompleteOrgOutDto {
    private String value;
    private String orgCd;
    private String orgNm;
}
