package com.shinhan.spp.dto.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AutoCompleteSearchInDto {
    private String query;
    private Integer cursor;
    private Integer size;
}
