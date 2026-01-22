package com.shinhan.spp.controller;

import com.shinhan.spp.annotation.ResponseDataOnly;
import com.shinhan.spp.domain.CommonGrpCode;
import com.shinhan.spp.dto.SalesDto;
import com.shinhan.spp.dto.out.CommonGrpCodeListDto;
import com.shinhan.spp.dto.out.CommonGrpCodePageDto;
import com.shinhan.spp.exception.custom.BusinessException;
import com.shinhan.spp.service.SampleService;
import com.shinhan.spp.util.ExcelExporter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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


     /** 1) @ExcelSheetKey 값 기준 자동 분할 (서부/동부 등) */
    @GetMapping("/excel/sales-by-hq")
    public void byHq(HttpServletResponse resp) {
        List<SalesDto> list = List.of(
            new SalesDto("서부", "수지점", "김대리", LocalDate.now().minusDays(2), "A상품", 10, 12000L, 120000L),
            new SalesDto("서부", "죽전점", "박과장", LocalDate.now().minusDays(1), "B상품", 5,  18000L,  90000L),
            new SalesDto("동부", "잠실점", "이차장", LocalDate.now(),          "A상품", 3,  12000L,  36000L)
        );
        Workbook wb = ExcelExporter.export(list, SalesDto.class);
        ExcelExporter.writeToResponse(wb, resp, "매출_본부별.xlsx");
    }

    /** 2) 리스트 2개 → 시트 2개 */
    @GetMapping("/excel/two-sheets")
    public void twoSheets(HttpServletResponse resp) {
        List<SalesDto> listA = List.of(
            new SalesDto("서부", "수지점", "김대리", LocalDate.now().minusDays(2), "A상품", 10, 12000L, 120000L)
        );
        List<SalesDto> listB = List.of(
            new SalesDto("동부", "잠실점", "이차장", LocalDate.now(), "B상품", 5, 18000L, 90000L),
            new SalesDto("동부", "송파점", "김차장", LocalDate.now(), "B상품", 5, 18000L, 90000L),
            new SalesDto("소계", "", "", null, "B상품", 10, 36000L, 90000L),
            new SalesDto("서부", "마곡점", "박차장", LocalDate.now(), "A상품", 5, 18000L, 90000L),
            new SalesDto("서부", "발산점", "최차장", LocalDate.now(), "A상품", 5, 18000L, 90000L),
            new SalesDto("소계", "", "", null, "", 10, 36000L, 180000L),
            new SalesDto("합계", "", "", null, "", 20, 72000L, 360000L)
        );



        Workbook wb = ExcelExporter.exportSheets(
            ExcelExporter.sheet("요약", listA, SalesDto.class),
            ExcelExporter.sheetWithTitle("상세", listB, SalesDto.class
                                        ,  ExcelExporter.title("11월 매출 상세", (short)16, true)
                                        ,row -> {
                                                try {
                                                    String g = (String) row.getClass().getMethod("getHq").invoke(row);
                                                    if ("합계".equals(g)) return new java.awt.Color(218, 238, 243); // #DAEEF3 연하늘
                                                    if ("소계".equals(g)) return new java.awt.Color(255, 242, 204); // #FFF2CC 연살구
                                                    return null; // 일반 행은 무배경
                                                } catch (Exception e) {
                                                    return null;
                                                }}
            )
        );
        ExcelExporter.writeToResponse(wb, resp, "두개시트.xlsx");
    }

}
