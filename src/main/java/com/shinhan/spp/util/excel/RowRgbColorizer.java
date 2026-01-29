// src/main/java/com/shinhan/spp/util/RowRgbColorizer.java
package com.shinhan.spp.util.excel;

/** (RGB) 행 배경색 결정 훅. null이면 무배경 */
@FunctionalInterface
public interface RowRgbColorizer {
    java.awt.Color color(Object row);
}
