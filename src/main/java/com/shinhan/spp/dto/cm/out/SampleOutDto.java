package com.shinhan.spp.dto.cm.out;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Getter
@Setter
@Builder
@Schema(description = "샘플 응답 DTO")
public class SampleOutDto {
    @Schema(description = "문자형", example = "")
    private String value1;

    @Schema(description = "숫자형", example = "")
    private Integer value2;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Schema(description = "일자형", example = "")
    private LocalDate value3;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    @Schema(description = "일시형", example = "")
    private LocalDateTime value4;
}
