package com.shinhan.spp.domain;

import com.shinhan.spp.annotation.ColumnMeta;
import com.shinhan.spp.annotation.DomainMeta;

@DomainMeta(name="USER_INFO", comment = "사용자")
public class UserInfo {
    @ColumnMeta(name = "USER_PKID", comment = "사용자PKID", notNull = false, length = 32)
    private String userPkid;
}
