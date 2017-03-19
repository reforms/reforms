package com.reforms.orm.dao.bobj.reader;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.reforms.orm.dao.column.SelectedColumn;

/**
 * Контракт на чтение значения Long из выборки ResultSet
 * @author evgenie
 */
class LongResultSetValueReader implements IResultSetValueReader<Long> {

    @Override
    public Long readValue(SelectedColumn column, ResultSet rs, Class<?> toBeClass) throws SQLException {
        long value = rs.getLong(column.getIndex());
        if (rs.wasNull()) {
            return null;
        }
        return value;
    }

}
