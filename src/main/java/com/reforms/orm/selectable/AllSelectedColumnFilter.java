package com.reforms.orm.selectable;

import com.reforms.ann.ThreadSafe;
import com.reforms.orm.select.SelectedColumn;

@ThreadSafe
public class AllSelectedColumnFilter implements ISelectedColumnFilter {

    @Override
    public boolean acceptSelectedColumn(SelectedColumn selectedColumn) {
        return true;
    }
}
