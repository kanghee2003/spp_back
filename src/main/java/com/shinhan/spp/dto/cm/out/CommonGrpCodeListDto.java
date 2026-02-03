package com.shinhan.spp.dto.cm.out;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CommonGrpCodeListDto {
    private Integer comGrpCdSeq;
    private String cmGrpCd;
    private String cmGrpNm;
    private String cmGrpDesc;
    private Boolean useFlag;
    private String ref01;
    private String ref02;
    private String ref03;
    private String ref04;
    private String ref05;
    private String ref06;
    private String ref07;
    private String ref08;
    private String ref09;
    private String ref10;
    private String ref11;
    private String ref12;
    private String ref13;
    private String ref14;
    private String ref15;
    private String strDate;


    public String getUuid() {
        return UUID.randomUUID().toString();
    }

//    public IudType getIudType() {
//        return null;
//    }
}
