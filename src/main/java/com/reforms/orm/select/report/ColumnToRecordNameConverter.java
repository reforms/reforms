package com.reforms.orm.select.report;

import java.util.concurrent.ConcurrentHashMap;

import com.reforms.orm.select.ColumnAlias;
import com.reforms.orm.select.SelectedColumn;

public class ColumnToRecordNameConverter implements IColumnToRecordNameConverter {

    private ConcurrentHashMap<String, String> fieldNames = new ConcurrentHashMap<>();

    @Override
    public String getRecordName(SelectedColumn column) {
        ColumnAlias cAlias = column.getColumnAlias();
        String metaFieldName = cAlias.getAliasKey();
        if (metaFieldName == null) {
            metaFieldName = column.getColumnName();
            if (metaFieldName == null) {
                return null;
            }
        }
        String newFieldName = fieldNames.get(metaFieldName);
        if (newFieldName == null) {
            newFieldName = convertRecordName(metaFieldName);
            fieldNames.putIfAbsent(metaFieldName, newFieldName);
        }
        return newFieldName;
    }

    protected String convertRecordName(String metaFieldName) {
        return metaFieldName.toUpperCase();
    }
}
