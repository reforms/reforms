package com.reforms.orm.dao.bobj.reader;

import com.reforms.orm.dao.column.SelectedColumn;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * Контракт на чтение значения Timestamp из выборки ResultSet
 * @author evgenie
 */
class TimestampResultSetValueReader implements IResultSetValueReader<Date> {

    @Override
    public Date readValue(SelectedColumn column, ResultSet rs, Class<?> toBeClass) throws SQLException {
        Date value = rs.getTimestamp(column.getIndex());
        if (rs.wasNull()) {
            return null;
        }
        if (Date.class == toBeClass) {
            value = new Date(value.getTime());
        }
        return value;
    }

}
