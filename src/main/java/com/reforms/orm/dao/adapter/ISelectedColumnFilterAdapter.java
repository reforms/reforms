package com.reforms.orm.dao.adapter;

import com.reforms.orm.dao.filter.column.ISelectedColumnFilter;

/**
 * Контракт на формирование фильтра колонок, для выборки
 * @author evgenie
 */
public interface ISelectedColumnFilterAdapter<Adapter> {

    Adapter addSelectableIndex(int toBeSelectedIndexColumn);

    Adapter addSelectableIndexes(int ... toBeSelectedIndexColumns);

    Adapter setSelectedColumnFilter(ISelectedColumnFilter filter);
}