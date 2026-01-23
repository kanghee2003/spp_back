package com.shinhan.spp.dto.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EditorImageUploadOutDto {
    private String url;
    private String originalName;
    private String storedName;
    private Long size;
}
