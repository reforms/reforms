package com.reforms.orm.dao.bobj.reader;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.reforms.orm.dao.column.SelectedColumn;

/**
 * Контракт на чтение значения Date из выборки ResultSet
 * @author evgenie
 */
class DateResultSetValueReader implements IResultSetValueReader<Date> {

    @Override
    public Date readValue(SelectedColumn column, ResultSet rs, Class<?> toBeClass) throws SQLException {
        java.sql.Date value = rs.getDate(column.getIndex());
        if (rs.wasNull()) {
            return null;
        }
        return value;
    }

}
