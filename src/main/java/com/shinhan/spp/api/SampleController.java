package com.shinhan.spp.api;

import com.shinhan.spp.annotation.ResponseDataOnly;
import com.shinhan.spp.domain.CommonGrpCode;
import com.shinhan.spp.dto.out.CommonGrpCodeListDto;
import com.shinhan.spp.dto.out.CommonGrpCodePageDto;
import com.shinhan.spp.exception.custom.BusinessException;
import com.shinhan.spp.service.SampleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 샘플 API 컨트롤러
 * @author 김강희
 */
@Tag(name = "SampleController", description = "샘플 API 컨트롤러")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/sample")
public class SampleController {
    private final SampleService sampleService;

    @Operation(summary = "tick", description = "tick")
    @GetMapping("/tick")
    public String test() {
        return sampleService.tick();
    }

    @Operation(summary = "test2", description = "test2")
    @GetMapping("/test2")
    public String test2() {
        return "1";
    }

    @Operation(summary = "test3", description = "ResponseDataOnly 사용예시")
    @ResponseDataOnly
    @GetMapping("/test3")
    public String test3()  {
        return "1";
    }

    @GetMapping("/grp-list")
    public List<CommonGrpCodeListDto> commonGrpCodeList(String searchText) throws Exception {
        return sampleService.selectCommonGrpCodeList(searchText);
    }

    /**
     * 서버 페이징 방식 목록 조회
     */
    @GetMapping("/grp-list-page")
    public CommonGrpCodePageDto commonGrpCodeListPage(
            @RequestParam(required = false) String searchText,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize
    ) throws Exception {
        return sampleService.selectCommonGrpCodeListPage(searchText, page, pageSize);
    }

    @PostMapping("/grp-save")
    public void commonGrpCodeSave(@RequestBody List<CommonGrpCode> commonCodeGrpList) throws Exception {
        sampleService.setCommonGrpCodeSave(commonCodeGrpList);
    }


    @Operation(summary = "error", description = "error")
    @GetMapping("/error")
    public String error() {
        if(true)
            throw new BusinessException("test");
        return null;
    }

}
