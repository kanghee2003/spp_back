package com.shinhan.spp.model;

import java.util.List;

public record UserContext(
        String userId,      // sub
        String orgCd,       // claim
        String userNm,      // claim (있으면)
        List<String> roles  // claim (권한)
) {}