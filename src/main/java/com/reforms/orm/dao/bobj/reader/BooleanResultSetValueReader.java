package com.reforms.orm.dao.bobj.reader;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.reforms.orm.dao.column.SelectedColumn;

/**
 * Контракт на чтение значения Boolean из выборки ResultSet
 * @author evgenie
 */
class BooleanResultSetValueReader implements IResultSetValueReader<Boolean> {

    @Override
    public Boolean readValue(SelectedColumn column, ResultSet rs, Class<?> toBeClass) throws SQLException {
        boolean value = rs.getBoolean(column.getIndex());
        if (rs.wasNull()) {
            return null;
        }
        return value;
    }

}
