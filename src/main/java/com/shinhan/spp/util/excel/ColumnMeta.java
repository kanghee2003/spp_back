package com.shinhan.spp.util.excel;

import com.shinhan.spp.enums.HAlign;

import java.lang.reflect.Field;
import java.util.List;

final class ColumnMeta {
    Field field;
    List<String> headerPath;
    int order;
    int widthChars;
    String format;
    HAlign align;
    boolean wrap;
    String fieldPath;
}
