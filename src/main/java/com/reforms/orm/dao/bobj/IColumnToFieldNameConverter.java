package com.reforms.orm.dao.bobj;

import com.reforms.orm.dao.column.SelectedColumn;

public interface IColumnToFieldNameConverter {

    public String getFieldName(SelectedColumn column);

}
