package com.shinhan.spp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AutoCompleteUserOutDto {
    private String value;
    private String userId;
    private String gradeCd;
    private String gradeNm;
    private String orgCd;
    private String orgNm;
    private String telNo;
}
