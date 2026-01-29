package com.shinhan.spp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DepartmentInfo(
        String departmentId,
        String departmentNo,
        String departmentName,
        String departmentPath,
        String departmentNamePath,
        String baseDepartmentYn
) {}
