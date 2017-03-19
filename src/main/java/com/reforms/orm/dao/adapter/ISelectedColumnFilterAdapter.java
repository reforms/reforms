package com.reforms.orm.dao.adapter;

import com.reforms.orm.selectable.ISelectedColumnFilter;

/**
 * Контракт на формирование фильтра колонок, для выборки
 * @author evgenie
 */
public interface ISelectedColumnFilterAdapter {

    Object addSelectableIndex(int toBeSelectedIndexColumn);

    Object addSelectableIndexes(int ... toBeSelectedIndexColumns);

    Object setSelectedColumnFilter(ISelectedColumnFilter filter);
}