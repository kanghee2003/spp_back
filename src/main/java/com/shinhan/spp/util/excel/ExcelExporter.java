package com.shinhan.spp.util.excel;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * ExcelExporter (POI 5.5.1 / SXSSF)
 *
 * - @ExcelColumn(header="상>중>하") 경로로 헤더 자동 병합(행/열)
 * - exportSheets(...) : 서로 다른 DTO 리스트 N개 → 시트 N개
 * - RowRgbColorizer(java.awt.Color) 행 배경 지원 (null 반환 시 무배경)
 * - 문자열 시간 파싱은 @ExcelColumn(format="time" | "hh:mm[:ss]") 컬럼만 (게이트)
 * - 문자열 개행(\n) 자동 래핑 (해당 셀만 wrapText) + @ExcelColumn.wrap=true면 강제 래핑
 * - 헤더 내부 그리드(THIN), 헤더 마지막 행 하단만 MEDIUM
 * - 헤더 배경 RGB 적용
 * - 타이틀(선택): 폰트 크기/굵기/정렬/행높이/배경(RGB 또는 없음) 동적 지정 (배경 기본 없음)
 */
public final class ExcelExporter {

    private ExcelExporter() {}

    /* ===== Public API ===== */

    /** 단일 시트 */
    public static <T> Workbook export(List<T> rows, Class<T> dtoClass) {
        SXSSFWorkbook wb = new SXSSFWorkbook(1000);
        wb.setCompressTempFiles(true);
        Styles styles = new Styles(wb);

        ColumnPlan plan = ColumnPlan.from(dtoClass);
        if (plan.columns.isEmpty()) throw new IllegalArgumentException("@ExcelColumn 최소 1개 필요");

        Sheet sh = wb.createSheet("Sheet1");
        buildSheet(sh, plan, new ArrayList<Object>(rows), styles, null, null);
        return wb;
    }

    /** 복수 시트 */
    @SafeVarargs
    public static Workbook exportSheets(SheetArg<?>... sheets) {
        SXSSFWorkbook wb = new SXSSFWorkbook(1000);
        wb.setCompressTempFiles(true);
        Styles styles = new Styles(wb);

        for (SheetArg<?> sa : sheets) {
            if (sa == null) continue;
            String name = trimSheetName(sa.sheetName);
            Sheet sh = wb.createSheet(name);

            ColumnPlan plan = ColumnPlan.from(sa.dtoClass);
            if (plan.columns.isEmpty())
                throw new IllegalArgumentException("시트 '" + name + "' DTO에 @ExcelColumn 최소 1개 필요");

            @SuppressWarnings("unchecked")
            List<Object> rows = (List<Object>) sa.rows;
            buildSheet(sh, plan, rows, styles, sa.rowRgbColorizer, sa.titleSpec);
        }
        return wb;
    }

    /** HTTP 응답 전송 */
    public static void writeToResponse(Workbook wb, HttpServletResponse resp, String filename) {
        try (Workbook _close = wb) {
            String enc = URLEncoder.encode(filename, StandardCharsets.UTF_8.name()).replaceAll("\\+", "%20");
            resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            resp.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + enc);
            wb.write(resp.getOutputStream());
            resp.flushBuffer();
        } catch (Exception ex) {
            throw new RuntimeException("엑셀 전송 실패", ex);
        } finally {
            if (wb instanceof SXSSFWorkbook) ((SXSSFWorkbook) wb).dispose();
        }
    }

    /* ===== SheetArg / 팩토리 ===== */

    public static <T> SheetArg<T> sheet(String name, List<T> rows, Class<T> dtoClass) {
        return new SheetArg<T>(name, rows, dtoClass, null, null);
    }
    /** 타이틀 없이 RGB 행 배경만 적용하고 싶을 때 사용 */
    public static <T> SheetArg<T> sheet(String name, List<T> rows, Class<T> dtoClass, RowRgbColorizer rgbColorizer) {
        return new SheetArg<T>(name, rows, dtoClass, rgbColorizer, null);
    }
    /** 타이틀 + RGB 행 배경 */
    public static <T> SheetArg<T> sheetWithTitle(String name, List<T> rows, Class<T> dtoClass,
                                                 TitleSpec title, RowRgbColorizer rgbColorizer) {
        return new SheetArg<T>(name, rows, dtoClass, rgbColorizer, title);
    }

    /** 타이틀 + RGB 행 배경 */
    public static <T> SheetArg<T> sheetWithTitle(String name, List<T> rows, Class<T> dtoClass,
                                                 TitleSpec title) {
        return new SheetArg<T>(name, rows, dtoClass, null, title);
    }

    // TitleSpec 팩토리
    public static TitleSpec title(String text) { return new TitleSpec(text); }
    public static TitleSpec title(String text, short fontPt, boolean bold) {
        return new TitleSpec(text, null, (short)20, HorizontalAlignment.CENTER, fontPt, bold);
    }
    public static TitleSpec title(String text, java.awt.Color rgb, short heightPt,
                                  HorizontalAlignment align, short fontPt, boolean bold) {
        return new TitleSpec(text, rgb, heightPt, align, fontPt, bold);
    }

    /* ===== Sheet build ===== */

    private static void buildSheet(Sheet sh, ColumnPlan plan, List<?> rows, Styles styles,
                                   RowRgbColorizer rgbColorizer, TitleSpec titleSpec) {

        List<ColumnMeta> leaves = plan.columns;
        int cols = leaves.size();
        HeaderLayout layout = HeaderLayout.from(leaves);
        int headerDepth = layout.maxDepth;

        // (0) 타이틀(옵션)
        int rowOffset = 0;
        if (titleSpec != null && titleSpec.text != null && !titleSpec.text.trim().isEmpty()) {
            Row trow = sh.createRow(0);
            trow.setHeightInPoints(titleSpec.heightPoints);
            Cell tc = trow.createCell(0);
            tc.setCellValue(titleSpec.text);
            tc.setCellStyle(styles.getTitleStyle(titleSpec));
            if (cols > 0) {
                CellRangeAddress region = new CellRangeAddress(0, 0, 0, cols - 1);
                sh.addMergedRegion(region);
            }
            rowOffset = 1;
        }

        // (A) 헤더 전체 그리드: THIN + 헤더 스타일
        for (int level = 0; level < headerDepth; level++) {
            Row r = sh.getRow(rowOffset + level);
            if (r == null) r = sh.createRow(rowOffset + level);
            for (int c = 0; c < cols; c++) {
                Cell cell = r.getCell(c);
                if (cell == null) cell = r.createCell(c);
                cell.setCellValue("");
                cell.setCellStyle(styles.header);
            }
        }

        // (B) 헤더 라벨 + 병합 + 병합영역 THIN 외곽
        for (int level = 0; level < headerDepth; level++) {
            Row r = sh.getRow(rowOffset + level);
            for (HeaderLayout.CellSpec cs : layout.rows.get(level)) {
                Cell cell = r.getCell(cs.startCol);
                if (cell == null) cell = r.createCell(cs.startCol);
                cell.setCellValue(cs.label);
                cell.setCellStyle(styles.header);

                int r1 = rowOffset + level;
                int r2 = rowOffset + level + cs.rowSpan - 1;
                int c1 = cs.startCol;
                int c2 = cs.startCol + cs.colSpan - 1;
                if (r1 != r2 || c1 != c2) {
                    CellRangeAddress region = new CellRangeAddress(r1, r2, c1, c2);
                    sh.addMergedRegion(region);
                    applyRegionBorderThin(region, sh); // 얇은선은 헤더 직후에 적용
                }
            }
        }

        // (B) 직후: 헤더 마지막 행 하단만 MEDIUM(RegionUtil 미사용)
        drawHeaderBottomBoldWithoutRegionUtil(sh, rowOffset, headerDepth, cols);

        // (C) 컬럼 폭
        for (int i = 0; i < cols; i++) {
            int w = Math.max(4, leaves.get(i).widthChars) * 256;
            sh.setColumnWidth(i, w);
        }

        // (D) 데이터 + 조건부 배경 (RGB)
        int rowIdx = rowOffset + headerDepth;
        for (Object row : rows) {
            Row r = sh.createRow(rowIdx++);
            java.awt.Color fillRgb = (rgbColorizer != null) ? rgbColorizer.color(row) : null;

            for (int c = 0; c < cols; c++) {
                ColumnMeta col = leaves.get(c);
                Object val = readValue(row, col);
                writeCell(r, c, val, col, styles, fillRgb);
            }
        }
    }

    /* ===== Borders ===== */

    private static void applyRegionBorderThin(CellRangeAddress region, Sheet sh) {
        RegionUtil.setBorderTop(BorderStyle.THIN, region, sh);
        RegionUtil.setBorderBottom(BorderStyle.THIN, region, sh);
        RegionUtil.setBorderLeft(BorderStyle.THIN, region, sh);
        RegionUtil.setBorderRight(BorderStyle.THIN, region, sh);
        short black = IndexedColors.BLACK.getIndex();
        RegionUtil.setTopBorderColor(black, region, sh);
        RegionUtil.setBottomBorderColor(black, region, sh);
        RegionUtil.setLeftBorderColor(black, region, sh);
        RegionUtil.setRightBorderColor(black, region, sh);
    }

    // 헤더 마지막 행 하단만 MEDIUM으로 굵게
    private static void drawHeaderBottomBoldWithoutRegionUtil(Sheet sh, int rowOffset, int headerDepth, int cols) {
        Row lastHeader = sh.getRow(rowOffset + headerDepth - 1);
        for (int c = 0; c < cols; c++) {
            Cell cell = lastHeader.getCell(c);
            if (cell == null) cell = lastHeader.createCell(c);
            CellStyle base = cell.getCellStyle();
            CellStyle s = sh.getWorkbook().createCellStyle();
            s.cloneStyleFrom(base);
            s.setBorderBottom(BorderStyle.MEDIUM);
            s.setBottomBorderColor(IndexedColors.BLACK.getIndex());
            cell.setCellStyle(s);
        }
    }

    /* ===== Numbers / Time ===== */

    private static final BigDecimal DOUBLE_SAFE_MAX = new BigDecimal("9007199254740991");  // 2^53-1
    private static final BigDecimal DOUBLE_SAFE_MIN = DOUBLE_SAFE_MAX.negate();

    private static boolean bigDecimalFitsDouble(BigDecimal bd) {
        if (bd == null) return true;
        if (bd.compareTo(DOUBLE_SAFE_MIN) < 0 || bd.compareTo(DOUBLE_SAFE_MAX) > 0) return false;
        int prec = bd.stripTrailingZeros().precision();
        return prec <= 15;
    }

    static String decimalFormatPattern(int scale) { // Styles에서 사용
        int s = Math.max(0, Math.min(scale, 12));
        StringBuilder sb = new StringBuilder("#,##0");
        if (s > 0) { sb.append('.'); for (int i = 0; i < s; i++) sb.append('#'); }
        return sb.toString();
    }

    // "hh:mm[:ss[.ms]]", "HHmmss", "HH:mm" → 엑셀 분수(0~1)
    private static Double tryParseTimeFractionFromString(String s) {
        if (s == null) return null;
        String str = s.trim();

        java.util.regex.Matcher m1 = java.util.regex.Pattern
                .compile("^(\\d{1,2}):(\\d{1,2})(?::(\\d{1,2})(?:\\.(\\d{1,9}))?)?$").matcher(str);
        if (m1.matches()) {
            int h = Integer.parseInt(m1.group(1));
            int min = Integer.parseInt(m1.group(2));
            int sec = (m1.group(3) != null) ? Integer.parseInt(m1.group(3)) : 0;
            int nano = 0;
            if (m1.group(4) != null) {
                String frac = m1.group(4);
                if (frac.length() > 9) frac = frac.substring(0, 9);
                nano = Integer.parseInt(frac) * (int)Math.pow(10, 9 - frac.length());
            }
            if (h > 23 || min > 59 || sec > 59) return null;
            return (h * 3600d + min * 60d + sec + nano / 1_000_000_000d) / 86400d;
        }

        if (str.matches("^\\d{6}$")) { // HHmmss
            int h = Integer.parseInt(str.substring(0,2));
            int min = Integer.parseInt(str.substring(2,4));
            int sec = Integer.parseInt(str.substring(4,6));
            if (h > 23 || min > 59 || sec > 59) return null;
            return (h * 3600d + min * 60d + sec) / 86400d;
        }

        java.util.regex.Matcher m2 = java.util.regex.Pattern.compile("^(\\d{1,2}):(\\d{1,2})$").matcher(str);
        if (m2.matches()) {
            int h = Integer.parseInt(m2.group(1));
            int min = Integer.parseInt(m2.group(2));
            if (h > 23 || min > 59) return null;
            return (h * 3600d + min * 60d) / 86400d;
        }
        return null;
    }

    // 문자열 컬럼이 "시간"으로 명시됐는지 판단 (게이트)
    private static boolean wantsTimeFormat(ColumnMeta col) {
        if (col == null || col.format == null) return false;
        String f = col.format.trim().toLowerCase(java.util.Locale.ROOT);
        return "time".equals(f) || f.contains("hh:mm");
    }

    /* ===== Reflection & write ===== */

    private static Object readValue(Object row, ColumnMeta col) {
        if (row == null) return null;

        // fieldPath 우선 (customer.name 같은 nested)
        if (col.fieldPath != null && !col.fieldPath.isEmpty()) {
            Object v = getByPath(row, col.fieldPath);
            if (v != null) return v;
        }

        // 기본: 필드명(getter/field)
        return getFieldValue(row, col.field.getName());
    }

    private static Object getByPath(Object bean, String path) {
        try {
            Object cur = bean;
            for (String p : path.split("\\.")) {
                if (cur == null) return null;
                String name = p.trim();
                if (name.isEmpty()) continue;
                cur = getFieldValue(cur, name);
            }
            return cur;
        } catch (Exception e) {
            return null;
        }
    }

    private static CellStyle pickStyle(Styles styles, ColumnMeta col, String kind, java.awt.Color rowFillRgb, boolean forceWrap) {
        CellStyle base = (rowFillRgb != null)
                ? styles.getDataStyleWithRgbFill(col, kind, rowFillRgb)
                : styles.getDataStyle(col, kind);

        if (forceWrap) return styles.wrapVariant(base);
        return base;
    }

    private static void writeCell(Row r, int c, Object val, ColumnMeta col, Styles styles, java.awt.Color rowFillRgb) {
        Cell cell = r.createCell(c);

        if (val == null) {
            cell.setCellValue("");
            cell.setCellStyle(pickStyle(styles, col, null, rowFillRgb, col.wrap));
            return;
        }

        // 시간: LocalTime / java.sql.Time
        if (val instanceof java.time.LocalTime) {
            java.time.LocalTime lt = (java.time.LocalTime) val;
            double fraction = (lt.toSecondOfDay() + lt.getNano() / 1_000_000_000d) / 86400d;
            cell.setCellValue(fraction);
            cell.setCellStyle(pickStyle(styles, col, "time", rowFillRgb, col.wrap));
            return;
        }
        if (val instanceof java.sql.Time) {
            java.time.LocalTime lt = ((java.sql.Time) val).toLocalTime();
            double fraction = (lt.toSecondOfDay() + lt.getNano() / 1_000_000_000d) / 86400d;
            cell.setCellValue(fraction);
            cell.setCellStyle(pickStyle(styles, col, "time", rowFillRgb, col.wrap));
            return;
        }

        // BigDecimal
        if (val instanceof BigDecimal) {
            BigDecimal bd = (BigDecimal) val;
            int scale = Math.max(0, bd.stripTrailingZeros().scale());
            if (bigDecimalFitsDouble(bd)) {
                cell.setCellValue(bd.doubleValue());
                cell.setCellStyle(pickStyle(styles, col, "decimal(" + scale + ")", rowFillRgb, col.wrap));
            } else {
                cell.setCellValue(bd.toPlainString());
                cell.setCellStyle(pickStyle(styles, col, "text", rowFillRgb, col.wrap));
            }
            return;
        }

        if (val instanceof Number) {
            Number num = (Number) val;
            cell.setCellValue(num.doubleValue());
            cell.setCellStyle(pickStyle(styles, col, "number", rowFillRgb, col.wrap));
            return;
        }

        if (val instanceof Boolean) {
            cell.setCellValue(((Boolean) val).booleanValue());
            cell.setCellStyle(pickStyle(styles, col, null, rowFillRgb, col.wrap));
            return;
        }

        // 날짜/일시 계열 (LocalTime 제외)
        if (isDateOrDateTime(val)) {
            Date d = toDate(val);
            if (d != null) {
                cell.setCellValue(d);
                cell.setCellStyle(pickStyle(styles, col, "date", rowFillRgb, col.wrap));
            } else {
                cell.setCellValue(String.valueOf(val));
                cell.setCellStyle(pickStyle(styles, col, "text", rowFillRgb, col.wrap));
            }
            return;
        }

        // 문자열: 시간 컬럼으로 명시된 경우에만 시간 파싱 시도 + 줄바꿈 래핑
        if (val instanceof String) {
            String s = (String) val;
            boolean hasNewline = (s.indexOf('\n') >= 0) || (s.indexOf('\r') >= 0);

            if (!hasNewline && wantsTimeFormat(col)) {
                Double fraction = tryParseTimeFractionFromString(s);
                if (fraction != null) {
                    cell.setCellValue(fraction.doubleValue());
                    cell.setCellStyle(pickStyle(styles, col, "time", rowFillRgb, col.wrap));
                    return;
                }
            }

            String normalized = s.replace("\r\n", "\n");
            CellStyle st = pickStyle(styles, col, "text", rowFillRgb, col.wrap || hasNewline);
            cell.setCellValue(normalized);
            cell.setCellStyle(st);
            return;
        }

        // 그 외
        cell.setCellValue(String.valueOf(val));
        cell.setCellStyle(pickStyle(styles, col, "text", rowFillRgb, col.wrap));
    }

    private static boolean isDateOrDateTime(Object v) {
        return (v instanceof Date)
                || (v instanceof Calendar)
                || (v instanceof java.time.LocalDate)
                || (v instanceof java.time.LocalDateTime)
                || (v instanceof java.time.ZonedDateTime)
                || (v instanceof java.time.OffsetDateTime)
                || (v instanceof java.time.Instant);
    }

    private static Date toDate(Object v) {
        if (v instanceof Date) return (Date) v;
        if (v instanceof Calendar) return ((Calendar) v).getTime();
        if (v instanceof java.time.LocalDate) {
            java.time.LocalDate ld = (java.time.LocalDate) v;
            return Date.from(ld.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
        }
        if (v instanceof java.time.LocalDateTime) {
            java.time.LocalDateTime ldt = (java.time.LocalDateTime) v;
            return Date.from(ldt.atZone(java.time.ZoneId.systemDefault()).toInstant());
        }
        if (v instanceof java.time.ZonedDateTime) {
            java.time.ZonedDateTime zdt = (java.time.ZonedDateTime) v;
            return Date.from(zdt.toInstant());
        }
        if (v instanceof java.time.OffsetDateTime) {
            java.time.OffsetDateTime odt = (java.time.OffsetDateTime) v;
            return Date.from(odt.toInstant());
        }
        if (v instanceof java.time.Instant) {
            java.time.Instant ins = (java.time.Instant) v;
            return Date.from(ins);
        }
        return null;
    }

    private static Object getFieldValue(Object bean, String name) {
        try {
            Field f = getField(bean.getClass(), name);
            if (f != null) { f.setAccessible(true); return f.get(bean); }

            String up = name.substring(0,1).toUpperCase() + name.substring(1);
            String[] tryNames = new String[] { "get" + up, "is" + up };
            for (String mn : tryNames) {
                try {
                    Method m = bean.getClass().getMethod(mn);
                    return m.invoke(bean);
                } catch (NoSuchMethodException ignore) {}
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private static Field getField(Class<?> c, String name) {
        for (Field f : getAllFields(c)) if (f.getName().equals(name)) return f;
        return null;
    }

    static List<Field> getAllFields(Class<?> c) { // ColumnPlan에서 사용
        List<Field> out = new ArrayList<>();
        for (Class<?> k = c; k != null && k != Object.class; k = k.getSuperclass()) {
            out.addAll(Arrays.asList(k.getDeclaredFields()));
        }
        return out;
    }

    private static String trimSheetName(String s) {
        String t = (s == null ? "Sheet" : s).trim();
        t = t.replaceAll("[\\\\/?*\\[\\]:]", "_");
        if (t.length() > 31) t = t.substring(0, 31);
        return t.isEmpty() ? "Sheet" : t;
    }
}
