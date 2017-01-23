package com.reforms.orm.select.bobj;

import com.reforms.orm.select.SelectedColumn;

public class ResultSetValueAdapter implements IResultSetValueAdapter {

    @Override
    public Object adapt(SelectedColumn column, Object rsValue, Class<?> toBeClass) {
        return rsValue;
    }
}
