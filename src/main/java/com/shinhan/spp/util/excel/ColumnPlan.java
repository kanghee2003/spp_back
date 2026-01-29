package com.shinhan.spp.util.excel;

import com.shinhan.spp.annotation.ExcelColumn;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

final class ColumnPlan {
    final List<ColumnMeta> columns;

    private ColumnPlan(List<ColumnMeta> columns) { this.columns = columns; }

    static <T> ColumnPlan from(Class<T> dtoClass) {
        List<ColumnMeta> list = new ArrayList<>();
        for (Field f : ExcelExporter.getAllFields(dtoClass)) {
            if (!f.isAnnotationPresent(ExcelColumn.class)) continue;

            ExcelColumn ec = f.getAnnotation(ExcelColumn.class);
            ColumnMeta m = new ColumnMeta();
            m.field = f;
            m.headerPath = Arrays.stream(ec.header().split(">"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
            m.order = ec.order();
            m.widthChars = ec.widthChars();
            m.format = ec.format() == null ? "" : ec.format().trim();
            m.align = ec.align();
            m.wrap = ec.wrap();
            m.fieldPath = ec.fieldPath() == null ? "" : ec.fieldPath().trim();
            list.add(m);
        }
        list.sort(Comparator.comparingInt(a -> a.order));
        return new ColumnPlan(list);
    }
}
