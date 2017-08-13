package com.reforms.orm.dao.bobj.reader;

import com.reforms.orm.dao.column.SelectedColumn;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Контракт на чтение значения Character из выборки ResultSet
 * @author evgenie
 */
class CharResultSetValueReader implements IResultSetValueReader<Character> {

    @Override
    public Character readValue(SelectedColumn column, ResultSet rs, Class<?> toBeClass) throws SQLException {
        String value = rs.getString(column.getIndex());
        if (rs.wasNull() || value.isEmpty()) {
            return null;
        }
        return value.charAt(0);
    }
}
