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
public class SalesDto {
    @ExcelColumn(header = "본부", order = 1, widthChars = 12)
    private String hq;

    @ExcelColumn(header = "기본정보>지점", order = 2, widthChars = 12)
    private String branch;

    @ExcelColumn(header = "기본정보>담당자", order = 3, widthChars = 12)
    private String manager;

    @ExcelColumn(header = "매출>일자", order = 4, format = "yyyy-mm-dd", align = HAlign.CENTER)
    private LocalDate saleDate;

    @ExcelColumn(header = "매출>구분>상품", order = 5, widthChars = 16)
    private String product;

    @ExcelColumn(header = "매출>구분>수량", order = 6, format = "#,##0", align = HAlign.RIGHT)
    private Integer qty;

    @ExcelColumn(header = "매출>금액>단가", order = 7, format = "#,##0", align = HAlign.RIGHT)
    private Long price;

    @ExcelColumn(header = "매출>금액>합계", order = 8, format = "#,##0", align = HAlign.RIGHT)
    private Long amount;

}
