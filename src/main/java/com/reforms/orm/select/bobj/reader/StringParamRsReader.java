package com.reforms.orm.select.bobj.reader;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.reforms.orm.select.SelectedColumn;

/**
 * Контракт на чтение значения String из выборки ResultSet
 * @author evgenie
 */
class StringParamRsReader implements IParamRsReader<String> {

    @Override
    public String readValue(SelectedColumn column, ResultSet rs, Class<?> toBeClass) throws SQLException {
        String value = rs.getString(column.getIndex());
        if (rs.wasNull()) {
            return null;
        }
        return value;
    }

}
