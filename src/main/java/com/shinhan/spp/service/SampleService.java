package com.shinhan.spp.service;

import com.shinhan.spp.dao.SampleDao;
import com.shinhan.spp.dto.in.SampleInDto;
import com.shinhan.spp.dto.out.SampleOutDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 샘플 API Service
 * @author 김강희
 */
@Service
@RequiredArgsConstructor
public class SampleService {
    private final SampleDao sampleDao;

    /**
     * 수행내용
     * @return 리턴값
     */
    @Transactional
    public String tick() {
        return sampleDao.tick();
    }

    /**
     * 수행내용
     * @param sampleInDto 파라미터설명
     * @return 리턴값
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public SampleOutDto selectSample(SampleInDto sampleInDto) {
        return sampleDao.selectSample(sampleInDto);
    }
}