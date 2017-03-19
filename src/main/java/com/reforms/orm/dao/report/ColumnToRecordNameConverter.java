package com.reforms.orm.dao.report;

import java.util.concurrent.ConcurrentHashMap;

import com.reforms.orm.dao.column.ColumnAlias;
import com.reforms.orm.dao.column.SelectedColumn;

public class ColumnToRecordNameConverter implements IColumnToRecordNameConverter {

    private ConcurrentHashMap<String, String> fieldNames = new ConcurrentHashMap<>();

    @Override
    public String getRecordName(SelectedColumn column) {
        ColumnAlias cAlias = column.getColumnAlias();
        String metaFieldName = cAlias.getJavaAliasKey();
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
