package com.shinhan.spp.controller;

import com.shinhan.spp.annotation.CurrentUser;
import com.shinhan.spp.annotation.ResponseDataOnly;
import com.shinhan.spp.domain.CommonGrpCode;
import com.shinhan.spp.dto.DetailDto;
import com.shinhan.spp.dto.SalesDto;
import com.shinhan.spp.dto.SummaryDto;
import com.shinhan.spp.dto.out.CommonGrpCodeListDto;
import com.shinhan.spp.dto.out.CommonGrpCodePageDto;
import com.shinhan.spp.dto.out.SampleFileUploadOutDto;
import com.shinhan.spp.exception.custom.BusinessException;
import com.shinhan.spp.model.UserContext;
import com.shinhan.spp.service.SampleFileService;
import com.shinhan.spp.service.SampleService;
import com.shinhan.spp.util.excel.ExcelExporter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    private final SampleFileService sampleFileService;

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
    public String test3(@CurrentUser UserContext user)  {
        return "1";
    }

    @GetMapping("/grp-list")
    public List<CommonGrpCodeListDto> commonGrpCodeList(String searchText,@CurrentUser UserContext user) throws Exception {
        return sampleService.selectCommonGrpCodeList(searchText);
    }

    /**
     * 서버 페이징 방식 목록 조회
     */
    @GetMapping("/grp-list-page")
    public CommonGrpCodePageDto commonGrpCodeListPage(
            @CurrentUser UserContext user,
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

    @Operation(summary = "샘플 파일 업로드", description = "샘플 파일 업로드")
    @PostMapping("/file/upload")
    public SampleFileUploadOutDto uploadSampleFile(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer seq
    ) throws Exception {
        return sampleFileService.saveSampleFiles(files, title, seq);
    }

    @Operation(summary = "샘플 파일 다운로드", description = "샘플 파일 다운로드")
    @GetMapping({"/file/download/{fileName:.+}"})
    public ResponseEntity<Resource> downloadSampleFile(@PathVariable("fileName") String fileName) throws Exception {
        return sampleFileService.getSampleFile(fileName);
    }

    @Operation(summary = "error", description = "error")
    @GetMapping("/error")
    public String error() {
        if(true)
            throw new BusinessException("test");
        return null;
    }

    /** 단일 시트 예제 (SummaryDto 한 장) */
    @GetMapping("/excel/one-sheet")
    public void oneSheet(HttpServletResponse resp) {
        List<SummaryDto> summary = List.of(
            new SummaryDto("서부", "수지점",
                    LocalDate.now().minusDays(7), LocalDate.now(),
                    12, 1_230_000L),
            new SummaryDto("동부", "잠실점",
                    LocalDate.now().minusDays(7), LocalDate.now(),
                    8,  980_000L)
        );

        Workbook wb = ExcelExporter.export(summary, SummaryDto.class);
        ExcelExporter.writeToResponse(wb, resp, "one-sheet-summary.xlsx");
    }

    /** 리스트 2개 → 시트 2개 예제 (Summary + Detail) */
    @GetMapping("/excel/two-sheets")
    public void twoSheets(HttpServletResponse resp) {
        List<SummaryDto> summary = List.of(
            new SummaryDto("서부", "수지점",
                    LocalDate.now().minusDays(7), LocalDate.now(),
                    12, 1_230_000L),
            new SummaryDto("동부", "잠실점",
                    LocalDate.now().minusDays(7), LocalDate.now(),
                    8,  980_000L)
        );

        List<DetailDto> detail = List.of(
            new DetailDto("서부", "수지점",
                    LocalDate.now().minusDays(2), "A상품", 10, 12_000L, 120_000L),
            new DetailDto("서부", "수지점",
                    LocalDate.now().minusDays(1), "B상품",  5, 18_000L,  90_000L),
            new DetailDto("동부", "잠실점",
                    LocalDate.now(), "A상품", 3, 12_000L, 36_000L)
        );

        Workbook wb = ExcelExporter.exportSheets(
            ExcelExporter.sheet("요약", summary, SummaryDto.class),
            ExcelExporter.sheet("상세", detail, DetailDto.class)
        );
        ExcelExporter.writeToResponse(wb, resp, "two-sheets-sample.xlsx");
    }



    @GetMapping("/excel/multiple-sheets")
    public void multipleSheets(HttpServletResponse resp) {
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
            ExcelExporter.sheetWithTitle("상세", listB, SalesDto.class,  ExcelExporter.title("11월 매출 상세", (short)16, true))
        );
        ExcelExporter.writeToResponse(wb, resp, "multiple-sheets-sample.xlsx");
    }

}
