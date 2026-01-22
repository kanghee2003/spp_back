
package com.shinhan.spp.annotation;

import com.shinhan.spp.enums.HAlign;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 각 DTO 필드에 다단 헤더 경로를 지정 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExcelColumn {
    /** 다단 헤더 경로: 예) "기본정보>지점" */
    String header();
    /** 출력 순서 (왼→오) */
    int order() default 9999;
    /** 컬럼 폭(엑셀 문자폭 기준) */
    int widthChars() default 14;
    /** 데이터 포맷(#,##0, yyyy-mm-dd 등). 미지정 시 자동 추론 */
    String format() default "";
    /** 정렬 */
    HAlign align() default HAlign.LEFT;
    /** 줄바꿈(강제 wrap) */
    boolean wrap() default false;
    /** 중첩 경로(옵션): "customer.name" 처럼 도트 표기 */
    String fieldPath() default "";
}

