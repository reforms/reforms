package com.reforms.orm.reflex;

/**
 * Контракт на создание объекта
 * @author evgenie
 */
public interface IInstanceBuilder {

    public void prepare();

    public void append(String metaFieldName, Object adaptedValue);

    public Object complete() throws Exception;
}