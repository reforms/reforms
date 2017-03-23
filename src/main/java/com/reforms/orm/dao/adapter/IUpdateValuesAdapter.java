package com.reforms.orm.dao.adapter;

import com.reforms.orm.dao.bobj.update.IUpdateValues;

import java.util.Map;

/**
 * Контракт на формирование данных для обновления
 * @author evgenie
 */
public interface IUpdateValuesAdapter<Adapter> {

    Adapter addUpdateValue(Object updateValue);

    Adapter addUpdateValues(Object ... updateValues);

    Adapter setUpdateObject(Object updateBobj);

    Adapter addUpdatePair(String paramName, Object updateValue);

    Adapter addUpdatePairs(Map<String, Object> updateValues);

    Adapter setUpdateValue(IUpdateValues updateValues);

}
