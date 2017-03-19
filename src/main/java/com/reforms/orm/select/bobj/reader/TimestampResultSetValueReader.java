package com.reforms.orm.select.bobj.reader;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.reforms.orm.select.SelectedColumn;

/**
 * Контракт на чтение значения Timestamp из выборки ResultSet
 * @author evgenie
 */
class TimestampResultSetValueReader implements IResultSetValueReader<Timestamp> {

    @Override
    public Timestamp readValue(SelectedColumn column, ResultSet rs, Class<?> toBeClass) throws SQLException {
        java.sql.Timestamp value = rs.getTimestamp(column.getIndex());
        if (rs.wasNull()) {
            return null;
        }
        return value;
    }

}
