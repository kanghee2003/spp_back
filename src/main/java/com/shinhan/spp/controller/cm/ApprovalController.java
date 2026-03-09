package com.shinhan.spp.controller.cm;

import com.shinhan.spp.annotation.ResponseDataOnly;
import com.shinhan.spp.annotation.UserInfo;
import com.shinhan.spp.domain.CommonGrpCode;
import com.shinhan.spp.dto.DetailDto;
import com.shinhan.spp.dto.SalesDto;
import com.shinhan.spp.dto.SummaryDto;
import com.shinhan.spp.dto.cm.in.CommonCodeSearchDto;
import com.shinhan.spp.dto.cm.out.CommonGrpCodeListDto;
import com.shinhan.spp.dto.cm.out.FileUploadOutDto;
import com.shinhan.spp.exception.custom.BusinessException;
import com.shinhan.spp.model.PageResponse;
import com.shinhan.spp.model.UserContext;
import com.shinhan.spp.service.SampleService;
import com.shinhan.spp.service.cm.ApprovalService;
import com.shinhan.spp.service.cm.FileService;
import com.shinhan.spp.util.excel.ExcelExporter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
 * 결재 샘플 API 컨트롤러
 *
 * @author 김강희OO
 */
@Tag(name = "ApprovalController", description = "결재 샘플 API 컨트롤러")
@RequiredArgsConstructor
@RestController
@RequestMapping("/sact")
public class ApprovalController {

    private final ApprovalService approvalService;


    @Operation(summary = "approval", description = "approval")
    @GetMapping("/approval")
    public void approval() throws Exception {
        approvalService.approval();
    }

}
