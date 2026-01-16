package com.shinhan.spp.dao;

import com.shinhan.spp.dto.in.SampleInDto;
import com.shinhan.spp.dto.out.SampleOutDto;
import org.apache.ibatis.annotations.Mapper;

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

    /**
     * 수행내용
     * @param sampleInDto 파라미터 설명
     * @return 리턴값
     */
    SampleOutDto selectSample(SampleInDto sampleInDto);
}
