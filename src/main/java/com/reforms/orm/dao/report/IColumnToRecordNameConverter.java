package com.reforms.orm.dao.report;

import com.reforms.orm.dao.column.SelectedColumn;

public interface IColumnToRecordNameConverter {

    public String getRecordName(SelectedColumn column);

}
