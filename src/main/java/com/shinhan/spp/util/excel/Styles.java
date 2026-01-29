package com.shinhan.spp.util.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

final class Styles {
    final Workbook wb;
    final DataFormat df;
    final CellStyle header;
    final Map<String, CellStyle> cache = new HashMap<>();
    final Font font;
    final Font fontBold;
    final IdentityHashMap<CellStyle, CellStyle> wrapCache = new IdentityHashMap<>();

    Styles(Workbook wb) {
        this.wb = wb;
        this.df = wb.createDataFormat();

        font = wb.createFont();
        font.setFontName("맑은 고딕");
        font.setFontHeightInPoints((short)10);

        fontBold = wb.createFont();
        fontBold.setFontName("맑은 고딕");
        fontBold.setFontHeightInPoints((short)10);
        fontBold.setBold(true);

        CellStyle h = wb.createCellStyle();
        h.setVerticalAlignment(VerticalAlignment.CENTER);
        h.setAlignment(HorizontalAlignment.CENTER);
        h.setWrapText(true);
        setBorderThin(h);

        // 헤더 배경 RGB (POI 5.x 정석)
        XSSFCellStyle xh = (XSSFCellStyle) h; // SXSSF도 XSSF 스타일 기반
        xh.setFillForegroundColor(new XSSFColor(new java.awt.Color(230, 242, 255), new DefaultIndexedColorMap())); // #E6F2FF
        h.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        h.setFont(fontBold);
        header = h;
    }

    CellStyle getTitleStyle(TitleSpec spec) {
        String key = "title|"
                + (spec.rgb == null ? "null" : (spec.rgb.getRed()+"-"+spec.rgb.getGreen()+"-"+spec.rgb.getBlue()))
                + "|" + spec.align + "|" + spec.fontPoints + "|" + spec.bold;
        if (cache.containsKey(key)) return cache.get(key);

        XSSFCellStyle s = (XSSFCellStyle) wb.createCellStyle();
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        s.setAlignment(spec.align);
        s.setWrapText(false);

        Font f = wb.createFont();
        f.setFontName("맑은 고딕");
        f.setBold(spec.bold);
        f.setFontHeightInPoints(spec.fontPoints);
        s.setFont(f);

        if (spec.rgb != null) { // 배경은 지정한 경우에만
            s.setFillForegroundColor(new XSSFColor(spec.rgb, new DefaultIndexedColorMap()));
            s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        } else {
            s.setFillPattern(FillPatternType.NO_FILL);
        }

        cache.put(key, s);
        return s;
    }

    CellStyle getDataStyle(ColumnMeta col, String kind) {
        String key = "base|" + col.align + "|" + col.wrap + "|" + (col.format == null || col.format.isEmpty() ? kind : col.format);
        if (cache.containsKey(key)) return cache.get(key);

        CellStyle s = wb.createCellStyle();
        setBorderThin(s);
        s.setVerticalAlignment(VerticalAlignment.CENTER);

        switch (col.align) {
            case CENTER: s.setAlignment(HorizontalAlignment.CENTER); break;
            case RIGHT:  s.setAlignment(HorizontalAlignment.RIGHT);  break;
            default:     s.setAlignment(HorizontalAlignment.LEFT);   break;
        }

        s.setWrapText(col.wrap);

        String fmt = (col.format == null) ? "" : col.format;
        if (fmt.isEmpty()) {
            if (kind != null && kind.startsWith("decimal(") && kind.endsWith(")")) {
                int scale = 0;
                try { scale = Integer.parseInt(kind.substring("decimal(".length(), kind.length()-1)); } catch (Exception ignore) {}
                fmt = ExcelExporter.decimalFormatPattern(scale);
            } else if ("time".equals(kind)) {
                fmt = "hh:mm:ss";
            } else if ("number".equals(kind)) {
                fmt = "#,##0";
            } else if ("date".equals(kind)) {
                fmt = "yyyy-mm-dd";
            } else {
                fmt = "@";
            }
        }
        s.setDataFormat(df.getFormat(fmt));
        s.setFont(font);
        cache.put(key, s);
        return s;
    }

    CellStyle getDataStyleWithRgbFill(ColumnMeta col, String kind, java.awt.Color rgb) {
        if (rgb == null) return getDataStyle(col, kind);
        String key = "rgb|" + rgb.getRed()+"-"+rgb.getGreen()+"-"+rgb.getBlue()
                + "|" + col.align + "|" + col.wrap + "|" + (col.format == null || col.format.isEmpty() ? kind : col.format);
        if (cache.containsKey(key)) return cache.get(key);

        CellStyle base = getDataStyle(col, kind);
        XSSFCellStyle s = (XSSFCellStyle) wb.createCellStyle();
        s.cloneStyleFrom(base);
        s.setFillForegroundColor(new XSSFColor(rgb, new DefaultIndexedColorMap()));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        cache.put(key, s);
        return s;
    }

    CellStyle wrapVariant(CellStyle base) {
        if (base.getWrapText()) return base;

        CellStyle cached = wrapCache.get(base);
        if (cached != null) return cached;

        CellStyle s = wb.createCellStyle();
        s.cloneStyleFrom(base);
        s.setWrapText(true);
        wrapCache.put(base, s);
        return s;
    }

    private static void setBorderThin(CellStyle s) {
        s.setBorderTop(BorderStyle.THIN);
        s.setBorderBottom(BorderStyle.THIN);
        s.setBorderLeft(BorderStyle.THIN);
        s.setBorderRight(BorderStyle.THIN);
    }
}
