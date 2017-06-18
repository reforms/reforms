package com.reforms.orm.dao;

import com.reforms.ann.TargetApi;

/**
 * Контракт на преобразование имени параметра
 * @author evgenie
 */
@FunctionalInterface
@TargetApi
public interface IParamNameConverter {

    public String convertName(int valueType, String name);
}
