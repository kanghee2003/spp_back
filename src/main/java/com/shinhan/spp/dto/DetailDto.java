package com.shinhan.spp.dto;


import com.shinhan.spp.annotation.ExcelColumn;
import com.shinhan.spp.enums.HAlign;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class DetailDto {

    @ExcelColumn(header = "기본정보>본부", order = 1, widthChars = 10)
    private String hq;

    @ExcelColumn(header = "기본정보>지점", order = 2, widthChars = 12)
    private String branch;

    @ExcelColumn(header = "매출>일자", order = 3, format = "yyyy-mm-dd", align = HAlign.CENTER, widthChars = 12)
    private LocalDate saleDate;

    @ExcelColumn(header = "매출>구분>상품명", order = 4, widthChars = 16)
    private String product;

    @ExcelColumn(header = "매출>구분>수량", order = 5, format = "#,##0", align = HAlign.RIGHT, widthChars = 8)
    private Integer qty;

    @ExcelColumn(header = "매출>금액>단가", order = 6, format = "#,##0", align = HAlign.RIGHT, widthChars = 10)
    private Long unitPrice;

    @ExcelColumn(header = "매출>금액>금액", order = 7, format = "#,##0", align = HAlign.RIGHT, widthChars = 12)
    private Long amount;
}
