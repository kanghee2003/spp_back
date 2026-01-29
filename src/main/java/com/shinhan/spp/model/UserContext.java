package com.shinhan.spp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserContext(
        @JsonProperty("sub")
        String userId,

        String profileUrl,

        List<RoleInfo> roles,
        List<DepartmentInfo> departments,
        List<OtherWorkerInfo> otherWorkers,

        String personalPhone,
        String companyName,
        String memo,

        String employeeNo,
        String employeeName,

        String positionName,
        String workLocation,
        String unitType,

        String webEmail,
        String companyNo,
        String companyEmail,
        String companyPhone,

        String profileImageUrl,

        String parentGwCmpCd,
        String departmentName,

        String chargeWork,
        String innerLinePhone,
        String parentCompanyCode,

        String absenteeismInfo,
        String faxNumber,

        Long iat,
        Long exp
) {
    public boolean hasRoleId(String roleId) {
        if (roleId == null || roles == null) return false;
        return roles.stream().anyMatch(r -> roleId.equals(r.roleId()));
    }
}
