package com.reforms.orm.dao.bobj;

import com.reforms.ann.TargetApi;
import com.reforms.orm.dao.column.SelectedColumn;

@FunctionalInterface
@TargetApi
public interface IColumnToFieldNameConverter {

    public String getFieldName(SelectedColumn column);

}
