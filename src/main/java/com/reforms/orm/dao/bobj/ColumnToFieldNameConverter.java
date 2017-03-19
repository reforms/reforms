package com.reforms.orm.dao.bobj;

import java.util.concurrent.ConcurrentHashMap;

import com.reforms.orm.dao.column.ColumnAlias;
import com.reforms.orm.dao.column.SelectedColumn;

public class ColumnToFieldNameConverter implements IColumnToFieldNameConverter {

    private ConcurrentHashMap<String, String> fieldNames = new ConcurrentHashMap<>();

    @Override
    public String getFieldName(SelectedColumn column) {
        ColumnAlias cAlias = column.getColumnAlias();
        String metaFieldName = cAlias != null ? cAlias.getJavaAliasKey() : null;
        if (metaFieldName == null) {
            metaFieldName = column.getColumnName();
            if (metaFieldName == null) {
                return null;
            }
        }
        String newFieldName = fieldNames.get(metaFieldName);
        if (newFieldName == null) {
            newFieldName = convertColumnName(metaFieldName);
            fieldNames.putIfAbsent(metaFieldName, newFieldName);
        }
        return newFieldName;
    }

    protected String convertColumnName(String metaFieldName) {
        StringBuilder newFieldName = new StringBuilder(metaFieldName.length() + 1);
        boolean makeUpper = false;
        for (char symbol : metaFieldName.toCharArray()) {
            if ('_' == symbol) {
                makeUpper = true;
                continue;
            }
            if (makeUpper) {
                symbol = Character.toUpperCase(symbol);
                makeUpper = false;
            }
            newFieldName.append(symbol);
        }
        return newFieldName.toString();
    }
}
