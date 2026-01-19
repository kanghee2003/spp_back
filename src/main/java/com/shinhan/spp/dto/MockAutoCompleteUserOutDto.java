package com.shinhan.spp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AutoComplete 무한스크롤(Mock) 아이템 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MockAutoCompleteUserOutDto {
    private String value;
    private String userId;
    private String gradeCd;
    private String gradeNm;
    private String orgCd;
    private String orgNm;
    private String telNo;
}
