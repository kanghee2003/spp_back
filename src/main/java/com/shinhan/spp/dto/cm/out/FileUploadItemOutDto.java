package com.shinhan.spp.dto.cm.out;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadItemOutDto {
    private String fileId;
    private String originalName;
    private String storedName;
    private long size;
}
