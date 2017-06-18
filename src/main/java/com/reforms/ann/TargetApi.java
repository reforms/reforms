package com.reforms.ann;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
/**
 * Аннотация маркер, помечам все классы, к которым даем доступ для работы и конфигурации извне
 * @author evgenie
 */
public @interface TargetApi {
}
