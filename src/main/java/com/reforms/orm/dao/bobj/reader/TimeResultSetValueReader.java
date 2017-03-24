package com.reforms.orm.dao.bobj.reader;

import com.reforms.orm.dao.column.SelectedColumn;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * Контракт на чтение значения Time из выборки ResultSet
 * @author evgenie
 */
class TimeResultSetValueReader implements IResultSetValueReader<Date> {

    @Override
    public Date readValue(SelectedColumn column, ResultSet rs, Class<?> toBeClass) throws SQLException {
        Date value = rs.getTime(column.getIndex());
        if (rs.wasNull()) {
            return null;
        }
        if (Date.class == toBeClass) {
            value = new Date(value.getTime());
        }
        return value;
    }
}
