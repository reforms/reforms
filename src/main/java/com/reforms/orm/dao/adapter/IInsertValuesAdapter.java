package com.reforms.orm.dao.adapter;

import java.util.Map;

import com.reforms.orm.dao.bobj.update.IInsertValues;

/**
 * Контракт на формирование данных для обновления
 * @author evgenie
 */
public interface IInsertValuesAdapter<Adapter> {

    Adapter addInsertValue(Object insertValue);

    Adapter addInsertValues(Object ... insertValues);

    Adapter setInsertObject(Object insertBobj);

    Adapter addInsertPair(String paramName, Object insertValue);

    Adapter addInsertPairs(Map<String, Object> insertValues);

    Adapter setInsertValue(IInsertValues insertValues);

}
