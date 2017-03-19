package com.reforms.orm.dao.bobj;

import com.reforms.orm.dao.column.SelectedColumn;

public interface IResultSetValueAdapter {

    public Object adapt(SelectedColumn column, Object rsValue, Class<?> toBeClass);
}
