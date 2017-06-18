package com.reforms.orm.dao.adapter;

import java.util.Iterator;
import java.util.Map;

import com.reforms.orm.dao.bobj.update.IUpdateValues;

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

    Adapter setBatchUpdateValues(Iterator<IUpdateValues> updateValues);

}
