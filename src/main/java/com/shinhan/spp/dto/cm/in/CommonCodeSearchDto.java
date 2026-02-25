package com.shinhan.spp.dto.cm.in;


import com.shinhan.spp.domain.CommonCode;
import com.shinhan.spp.enums.IudType;
import com.shinhan.spp.model.PageRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonCodeSearchDto extends PageRequest {
    private String searchText;

}
