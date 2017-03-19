package com.reforms.orm.dao.adapter;

import java.util.Map;

import com.reforms.orm.dao.filter.IFilterValues;

/**
 * Контракт на формирование фильтра значений
 * @author evgenie
 */
public interface IFilterValuesAdapter<Adapter> {

    Adapter addSimpleFilterValue(Object filterValue);

    Adapter addSimpleFilterValues(Object ... filterValues);

    Adapter setFilterObject(Object filterBobj);

    Adapter addFilterPair(String paramName, Object filterValue);

    Adapter addFilterPairs(Map<String, Object> filterValues);

    Adapter setFilterValue(IFilterValues filter);

}
