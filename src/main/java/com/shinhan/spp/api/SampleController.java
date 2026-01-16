package com.shinhan.spp.api;

import com.shinhan.spp.annotation.ResponseDataOnly;
import com.shinhan.spp.dto.SampleDto;
import com.shinhan.spp.dto.in.SampleInDto;
import com.shinhan.spp.dto.out.SampleOutDto;
import com.shinhan.spp.exception.custom.BusinessException;
import com.shinhan.spp.service.SampleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @Operation(summary = "select", description = "select")
    @GetMapping("/select-dto")
    public SampleOutDto select(SampleInDto sampleInDto) {
        return sampleService.selectSample(sampleInDto);
    }

    @Operation(summary = "error", description = "error")
    @GetMapping("/error")
    public String error() {
        if(true)
            throw new BusinessException("test");
        return null;
    }
}
