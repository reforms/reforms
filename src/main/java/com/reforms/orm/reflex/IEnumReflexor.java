package com.reforms.orm.reflex;

/**
 * Контракт на получение нужной информации
 * @author evgenie
 */
public interface IEnumReflexor {

    Object getAssignValue(Object enumValue);

    Object getEnumValue(Object assignValue);

    Class<?> getAssignValueClass();
}