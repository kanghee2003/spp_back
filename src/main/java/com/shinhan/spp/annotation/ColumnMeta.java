package com.shinhan.spp.annotation;

import java.lang.annotation.*;

/**
 * Column(필드) 수준의 메타 정보를 선언하기 위한 어노테이션.
 *
 * - notNull: DB 제약조건 NOT NULL 의미(스키마 기준)
 * - nullable: 애플리케이션/DTO 관점에서 null 허용 여부
 *   (둘이 중복되는 느낌이 있긴 해서, 실사용에서는 하나만 쓰는 팀도 많아요.)
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ColumnMeta {

    /**
     * 실제 컬럼명.
     */
    String name() default "";

    /**
     * 컬럼 설명(코멘트)
     */
    String comment() default "";

    /**
     * DB 스키마 기준 NOT NULL 여부
     */
    boolean notNull() default false;

    /**
     * 문자열 컬럼 등의 최대 길이. 모르면 -1.
     */
    int length() default -1;
}
