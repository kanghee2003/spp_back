package com.shinhan.spp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RoleInfo(
        String roleId,
        String roleName
) {}
