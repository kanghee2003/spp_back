package com.shinhan.spp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AutoCompleteOrgOutDto {
    private String value;
    private String orgCd;
    private String orgNm;
}
