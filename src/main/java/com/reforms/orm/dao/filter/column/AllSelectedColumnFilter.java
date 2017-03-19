package com.reforms.orm.dao.filter.column;

import com.reforms.ann.ThreadSafe;
import com.reforms.orm.dao.column.SelectedColumn;

@ThreadSafe
public class AllSelectedColumnFilter implements ISelectedColumnFilter {

    public static final ISelectedColumnFilter ALL_COLUMNS_FILTER = new AllSelectedColumnFilter();

    @Override
    public boolean acceptSelectedColumn(SelectedColumn selectedColumn) {
        return true;
    }
}
