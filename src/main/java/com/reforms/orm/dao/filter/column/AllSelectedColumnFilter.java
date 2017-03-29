package com.reforms.orm.dao.filter.column;

import com.reforms.ann.ThreadSafe;
import com.reforms.orm.dao.column.SelectedColumn;

import static com.reforms.orm.dao.filter.column.FilterState.FS_ACCEPT;

/**
 * Все колонки
 * @author evgenie
 */
@ThreadSafe
public class AllSelectedColumnFilter implements ISelectedColumnFilter {

    public static final ISelectedColumnFilter ALL_COLUMNS_FILTER = new AllSelectedColumnFilter();

    @Override
    public FilterState acceptSelectedColumn(SelectedColumn selectedColumn) {
        return FS_ACCEPT;
    }
}