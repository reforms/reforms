package com.reforms.orm.dao;

/**
 * Контракт на преобразование имени параметра
 * @author evgenie
 */
public interface IParamNameConverter {

    public String convertName(int valueType, String name);
}
