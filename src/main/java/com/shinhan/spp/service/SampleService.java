package com.shinhan.spp.service;

import com.shinhan.spp.dao.SampleDao;
import com.shinhan.spp.domain.CommonGrpCode;
import com.shinhan.spp.dto.CommonCodeSaveDto;
import com.shinhan.spp.dto.in.CommonCodeListParamDto;
import com.shinhan.spp.dto.in.SampleInDto;
import com.shinhan.spp.dto.out.CommonCodeListDto;
import com.shinhan.spp.dto.out.CommonGrpCodeListDto;
import com.shinhan.spp.dto.out.CommonGrpCodePageDto;
import com.shinhan.spp.dto.out.SampleOutDto;
import com.shinhan.spp.enums.IudType;
import com.shinhan.spp.exception.custom.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Transactional
    public List<CommonGrpCodeListDto> selectCommonGrpCodeList(String searchText) throws Exception {
        return sampleDao.selectCommonGrpCodeList(searchText);
    }

    @Transactional
    public CommonGrpCodePageDto selectCommonGrpCodeListPage(String searchText, Integer page, Integer pageSize) throws Exception {
        int safePage = (page == null || page < 1) ? 1 : page;
        int safeSize = (pageSize == null || pageSize < 1) ? 10 : Math.min(pageSize, 200);

        int offset = (safePage - 1) * safeSize;
        Integer total = sampleDao.countCommonGrpCodeList(searchText);
        List<CommonGrpCodeListDto> items = sampleDao.selectCommonGrpCodeListPage(searchText, offset, safeSize);

        return CommonGrpCodePageDto.builder()
                .items(items)
                .page(safePage)
                .pageSize(safeSize)
                .totalCount(total == null ? 0 : total)
                .build();
    }


    @Transactional
    public void setCommonGrpCodeSave(List<CommonGrpCode> commonCodeGrpList) throws Exception {
        for (CommonGrpCode commonGrpCode :commonCodeGrpList) {
            commonGrpCode.setRgstUserId("System");
            commonGrpCode.setUptUserId("System");
            if (commonGrpCode.getIudType() == IudType.I) {
                sampleDao.insertCommonGrpCode(commonGrpCode);
                if(true)
                    throw new BusinessException("XXX");
            } else if (commonGrpCode.getIudType() == IudType.U) {
                sampleDao.updateCommonGrpCode(commonGrpCode);
            } else if (commonGrpCode.getIudType() == IudType.D) {
                sampleDao.deleteCommonGrpCode(commonGrpCode.getComGrpCdSeq());
                sampleDao.deleteCommonCodeGroup(commonGrpCode.getComGrpCdSeq());
            }
        }
    }

    @Transactional
    public List<CommonCodeListDto> selectCommonCodeList(CommonCodeListParamDto param) throws Exception {
        return sampleDao.selectCommonCodeList(param);
    }

    @Transactional
    public void setCommonCodeSave(List<CommonCodeSaveDto> commonCodeGrpList) throws Exception {

        for (CommonCodeSaveDto commonCode :commonCodeGrpList) {
            if(commonCode.getIudType() != null) {
                commonCode.setRgstUserId("System");
                commonCode.setUptUserId("System");
                commonCode.setCmStdCd(commonCode.getCmGrpCd().concat(commonCode.getCmCd()));
                if (commonCode.getIudType() == IudType.I) {
                    sampleDao.insertCommonCode(commonCode);
                } else if (commonCode.getIudType() == IudType.U) {
                    sampleDao.updateCommonCode(commonCode);
                } else if (commonCode.getIudType() == IudType.D) {
                    sampleDao.deleteCommonCode(commonCode.getComCdSeq());
                }
            }

        }
    }
}