package com.shinhan.spp.dto.in;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
@Schema(description = "샘플 요청 DTO")
public class SampleInDto {
    @Schema(description = "값", example = "1")
    private String val;
}
