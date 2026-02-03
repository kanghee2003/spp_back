package com.shinhan.spp.dto.cm;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileMetaDto {
    private String fileId;
    private String filePath;
    private String savedFileName;  // storedName
    private String orgFileName;    // originalName
}
