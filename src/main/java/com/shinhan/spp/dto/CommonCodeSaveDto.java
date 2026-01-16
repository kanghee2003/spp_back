package com.shinhan.spp.dto;


import com.shinhan.spp.domain.CommonCode;
import com.shinhan.spp.enums.IudType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonCodeSaveDto extends CommonCode {
    private String cmGrpCd;
    private IudType iudType;

}
