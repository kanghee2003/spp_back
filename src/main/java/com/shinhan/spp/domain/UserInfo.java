package com.shinhan.spp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shinhan.spp.annotation.ColumnMeta;
import com.shinhan.spp.annotation.DomainMeta;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@DomainMeta(name="USER_INFO", comment = "사용자")
public class UserInfo {
private Integer userSeq;
    private String userId;
    @JsonIgnore
    private String passwd;
    @JsonIgnore
    private String salt;
    private String userName;
    private String userNameEng;
    private String nickName;
    private String userCd;
    private String telNo;
    private String hpNo;
    private String deptCd;
    private String email;
    private String workCd;
    private LocalDate strDate;
    private LocalDate endDate;
    private LocalDateTime loginDateTime;
    private Boolean useFlag;
    private Boolean admFlag;
    private String rgstUserId;
    private LocalDateTime rgstDateTime;
    private String uptUserId;
    private LocalDateTime uptDateTime;
}
