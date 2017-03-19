package com.reforms.orm.selectable;

import com.reforms.ann.ThreadSafe;
import com.reforms.orm.select.SelectedColumn;

@ThreadSafe
public class AllSelectedColumnFilter implements ISelectedColumnFilter {

    public static final ISelectedColumnFilter ALL_COLUMNS_FILTER = new AllSelectedColumnFilter();

    @Override
    public boolean acceptSelectedColumn(SelectedColumn selectedColumn) {
        return true;
    }
}
