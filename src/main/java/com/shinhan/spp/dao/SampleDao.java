package com.shinhan.spp.dao;

import com.shinhan.spp.domain.CommonCode;
import com.shinhan.spp.domain.CommonGrpCode;
import com.shinhan.spp.domain.UserInfo;
import com.shinhan.spp.dto.cm.in.CommonCodeListParamDto;
import com.shinhan.spp.dto.cm.in.CommonCodeSearchDto;
import com.shinhan.spp.dto.cm.in.SampleInDto;
import com.shinhan.spp.dto.cm.out.CommonCodeListDto;
import com.shinhan.spp.dto.cm.out.CommonGrpCodeListDto;
import com.shinhan.spp.dto.cm.out.SampleOutDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 샘플 DAO
 * @author 김강희
 */
@Mapper
public interface SampleDao {
    /**
     * 수행내용
     * @return 리턴값
     */
    String tick();



    UserInfo selectUserInfo(String userId);


    /**
     * 수행내용
     * @param sampleInDto 파라미터 설명
     * @return 리턴값
     */
    SampleOutDto selectSample(SampleInDto sampleInDto);


    List<CommonGrpCodeListDto> selectCommonGrpCodeList(@Param("searchText") String searchText) throws Exception;

    Integer countCommonGrpCodeList(@Param("searchText") String searchText) throws Exception;

    List<CommonGrpCodeListDto> selectCommonGrpCodeListPage(CommonCodeSearchDto param) throws Exception;
    void insertCommonGrpCode(CommonGrpCode commonGrpCode) throws Exception;
    void updateCommonGrpCode(CommonGrpCode commonGrpCode) throws Exception;
    void deleteCommonGrpCode(@Param("comGrpCdSeq") Integer comGrpCdSeq) throws Exception;
    void deleteCommonCodeGroup(@Param("comGrpCdSeq") Integer comGrpCdSeq) throws Exception;
    List<CommonCodeListDto> selectCommonCodeList(CommonCodeListParamDto param) throws Exception;
    void insertCommonCode(CommonCode commonGrpCode) throws Exception;
    void updateCommonCode(CommonCode commonGrpCode) throws Exception;
    void deleteCommonCode(@Param("comCdSeq") Integer comCdSeq) throws Exception;
}
