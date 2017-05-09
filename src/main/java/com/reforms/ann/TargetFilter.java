package com.reforms.ann;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface TargetFilter {

    /**
     * Наименование парамета фильтра в запросе.
     * Parameter name for query
     */
    String value() default "";

    /**
     * Признак того, что это орм фильтр
     */
    boolean bobj() default false;

}
