package com.reforms.orm.dao.bobj;

import com.reforms.orm.dao.column.SelectedColumn;

public class ResultSetValueAdapter implements IResultSetValueAdapter {

    @Override
    public Object adapt(SelectedColumn column, Object rsValue, Class<?> toBeClass) {
        return rsValue;
    }
}
