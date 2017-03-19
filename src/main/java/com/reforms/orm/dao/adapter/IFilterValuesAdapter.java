package com.reforms.orm.dao.adapter;

import java.util.Map;

import com.reforms.orm.filter.IFilterValues;

/**
 * Контракт на формирование фильтра значений
 * @author evgenie
 */
public interface IFilterValuesAdapter {

    Object addSimpleFilterValue(Object filterValue);

    Object addSimpleFilterValues(Object ... filterValues);

    Object setFilterObject(Object filterBobj);

    Object addFilterPair(String paramName, Object filterValue);

    Object addFilterPairs(Map<String, Object> filterValues);

    Object setFilterValue(IFilterValues filter);

}
