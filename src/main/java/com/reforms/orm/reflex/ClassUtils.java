package com.reforms.orm.reflex;

/**
 * Утилитные методы по работе с классами
 * @author evgenie
 */
public class ClassUtils {

    public static boolean isEnumClass(Class<?> clazz) {
        return (clazz != null && (clazz.isEnum() || (clazz.isAnonymousClass() && clazz.getSuperclass().isEnum())));
    }
}
