package com.reforms.orm.select.bobj;

import com.reforms.orm.select.SelectedColumn;

public interface IResultSetValueAdapter {

    public Object adapt(SelectedColumn column, Object rsValue, Class<?> toBeClass);
}
