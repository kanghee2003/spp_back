package com.shinhan.spp.model;


import com.shinhan.spp.dto.cm.out.CommonGrpCodeListDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class PageRequest {
    @Schema(description = "page번호", example = "")
    private Integer page;

    @Schema(description = "page건수", example = "")
    private Integer pageSize;

    public Integer getLimit() {
        return (pageSize == null || pageSize < 1) ? 10 : Math.min(pageSize, 100);
    }

    public Integer getOffset() {
        return (((page == null || page < 1) ? 1 : page) -1) * this.getLimit();
    }

}
