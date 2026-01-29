package com.shinhan.spp.util.excel;

import java.util.List;

public final class SheetArg<T> {
    final String sheetName;
    final List<T> rows;
    final Class<T> dtoClass;
    final RowRgbColorizer rowRgbColorizer;   // 선택
    final TitleSpec titleSpec;               // 선택(null=타이틀 없음)

    SheetArg(String sheetName, List<T> rows, Class<T> dtoClass,
             RowRgbColorizer rgbColorizer, TitleSpec titleSpec) {
        this.sheetName = sheetName;
        this.rows = rows;
        this.dtoClass = dtoClass;
        this.rowRgbColorizer = rgbColorizer;
        this.titleSpec = titleSpec;
    }
}
