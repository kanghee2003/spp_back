package com.shinhan.spp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OtherWorkerInfo(
        String companyCode,
        String positionName,
        String employeeName,
        String employeeNo
) {}
