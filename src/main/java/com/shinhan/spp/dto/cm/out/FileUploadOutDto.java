package com.shinhan.spp.dto.cm.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadOutDto {
    private String title;
    private Integer seq;
    private Integer fileCount;
    private List<FileUploadItemOutDto> files;
}
