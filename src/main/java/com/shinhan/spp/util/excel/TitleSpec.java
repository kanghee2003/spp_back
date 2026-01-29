package com.shinhan.spp.util.excel;

import org.apache.poi.ss.usermodel.HorizontalAlignment;

/** 첫 줄 타이틀 옵션 (null이면 타이틀 없음) — 배경은 기본적으로 적용하지 않음 */
public final class TitleSpec {
    final String text;
    final java.awt.Color rgb;        // null이면 배경 없음(투명)
    final short heightPoints;        // 행 높이 (pt)
    final HorizontalAlignment align; // 정렬
    final short fontPoints;          // 폰트 크기 (pt)
    final boolean bold;              // 굵게

    // 기본: 배경 없음, 20pt, 가운데, 14pt, Bold
    public TitleSpec(String text) {
        this(text, null, (short)20, HorizontalAlignment.CENTER, (short)14, true);
    }
    public TitleSpec(String text, java.awt.Color rgb) {
        this(text, rgb, (short)20, HorizontalAlignment.CENTER, (short)14, true);
    }
    public TitleSpec(String text, java.awt.Color rgb, short heightPoints, HorizontalAlignment align) {
        this(text, rgb, heightPoints, align, (short)14, true);
    }
    public TitleSpec(String text, java.awt.Color rgb, short heightPoints,
                     HorizontalAlignment align, short fontPoints, boolean bold) {
        this.text = text;
        this.rgb = rgb; // null => 배경 없음
        this.heightPoints = heightPoints;
        this.align = (align == null ? HorizontalAlignment.CENTER : align);
        this.fontPoints = (fontPoints <= 0 ? (short)14 : fontPoints);
        this.bold = bold;
    }
}
