package com.reforms.orm.dao.report;

import com.reforms.ann.TargetApi;
import com.reforms.orm.dao.column.SelectedColumn;

@FunctionalInterface
@TargetApi
public interface IColumnToRecordNameConverter {

    public String getRecordName(SelectedColumn column);

}
