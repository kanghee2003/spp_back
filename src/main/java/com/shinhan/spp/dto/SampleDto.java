package com.shinhan.spp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
@Schema(description = "샘플 응답 DTO")
public class SampleDto {
    @Schema(description = "값", example = "1")
    private String val;
}
