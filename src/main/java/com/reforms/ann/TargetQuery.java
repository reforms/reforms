package com.reforms.ann;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TargetQuery {

    /** Select query */
    final int ST_AUTO = -1;

    /** Select query */
    final int ST_SELECT = 0;

    /** Insert query */
    final int ST_INSERT = 1;

    /** Update query */
    final int ST_UPDATE = 2;

    /** Delete query */
    final int ST_DELETE = 3;

    /** Тип sql-запроса */
    int type() default ST_AUTO;

    /** SQL-запрос */
    String query();

    /** SQL-запрос */
    Class<?> orm() default Object.class;

}
