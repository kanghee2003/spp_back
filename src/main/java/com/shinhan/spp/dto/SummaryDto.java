package com.shinhan.spp.dto;

import com.shinhan.spp.annotation.ExcelColumn;
import com.shinhan.spp.enums.HAlign;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class SummaryDto {
    @ExcelColumn(header = "기본정보>본부", order = 1, widthChars = 10)
    private String hq;

    @ExcelColumn(header = "기본정보>지점", order = 2, widthChars = 12)
    private String branch;

    @ExcelColumn(header = "기간>시작일", order = 3, format = "yyyy-mm-dd", align = HAlign.CENTER, widthChars = 12)
    private LocalDate startDate;

    @ExcelColumn(header = "기간>종료일", order = 4, format = "yyyy-mm-dd", align = HAlign.CENTER, widthChars = 12)
    private LocalDate endDate;

    @ExcelColumn(header = "집계>건수", order = 5, format = "#,##0", align = HAlign.RIGHT, widthChars = 8)
    private Integer cnt;

    @ExcelColumn(header = "집계>합계금액", order = 6, format = "#,##0", align = HAlign.RIGHT, widthChars = 12)
    private Long totalAmount;
}
