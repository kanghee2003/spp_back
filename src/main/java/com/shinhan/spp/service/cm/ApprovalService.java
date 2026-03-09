package com.shinhan.spp.service.cm;

import com.shinhan.spp.annotation.validator.ApprovalMethodRegistry;
import com.shinhan.spp.dao.SampleDao;
import com.shinhan.spp.domain.CommonGrpCode;
import com.shinhan.spp.domain.UserInfo;
import com.shinhan.spp.dto.cm.ApprovalParam;
import com.shinhan.spp.dto.cm.CommonCodeSaveDto;
import com.shinhan.spp.dto.cm.in.CommonCodeListParamDto;
import com.shinhan.spp.dto.cm.in.CommonCodeSearchDto;
import com.shinhan.spp.dto.cm.in.SampleInDto;
import com.shinhan.spp.dto.cm.out.CommonCodeListDto;
import com.shinhan.spp.dto.cm.out.CommonGrpCodeListDto;
import com.shinhan.spp.dto.cm.out.SampleOutDto;
import com.shinhan.spp.enums.IudType;
import com.shinhan.spp.exception.custom.BusinessException;
import com.shinhan.spp.internal.service.UserContextEvictService;
import com.shinhan.spp.model.PageResponse;
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
public class ApprovalService {

    private final ApprovalMethodRegistry registry;

    @Transactional
    public void approval()  {
        ApprovalParam param = new ApprovalParam();
        param.setResult("SUCCESS");
        registry.run("A", param);
    }
}