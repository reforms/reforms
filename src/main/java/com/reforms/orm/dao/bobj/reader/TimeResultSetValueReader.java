package com.reforms.orm.dao.bobj.reader;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;

import com.reforms.orm.dao.column.SelectedColumn;

/**
 * Контракт на чтение значения Time из выборки ResultSet
 * @author evgenie
 */
class TimeResultSetValueReader implements IResultSetValueReader<Time> {

    @Override
    public Time readValue(SelectedColumn column, ResultSet rs, Class<?> toBeClass) throws SQLException {
        java.sql.Time value = rs.getTime(column.getIndex());
        if (rs.wasNull()) {
            return null;
        }
        return value;
    }

}
