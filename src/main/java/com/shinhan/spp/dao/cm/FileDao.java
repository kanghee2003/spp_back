package com.shinhan.spp.dao.cm;

import com.shinhan.spp.dto.cm.FileMetaDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 파일 메타 DAO
 */
@Mapper
public interface FileDao {

    void insertFile(FileMetaDto dto);

    FileMetaDto selectFileMeta(@Param("fileId") Long fileId);
}
