package com.shinhan.spp.dto.cm.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 공통 그룹코드 목록 - 페이징 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonGrpCodePageDto {
    private List<CommonGrpCodeListDto> items;
    private Integer page;
    private Integer pageSize;
    private Integer totalCount;
}
