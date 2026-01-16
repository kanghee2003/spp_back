package com.shinhan.spp.annotation;

import java.lang.annotation.*;

/**
 * Entity(Table) 수준의 메타 정보를 선언하기 위한 어노테이션.
 * 예) 테이블 코멘트/설명
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DomainMeta {

    /**
     * 실제 테이블명
     */
    String name() default "";

    /**
     * 테이블 설명(코멘트)
     */
    String comment() default "";


}
