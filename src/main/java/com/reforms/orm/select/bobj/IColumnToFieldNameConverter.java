package com.reforms.orm.select.bobj;

import com.reforms.orm.select.SelectedColumn;

public interface IColumnToFieldNameConverter {

    public String getFieldName(SelectedColumn column);

}
