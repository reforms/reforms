package com.reforms.orm.select.report;

import com.reforms.orm.select.SelectedColumn;

public interface IColumnToRecordNameConverter {

    public String getRecordName(SelectedColumn column);

}
