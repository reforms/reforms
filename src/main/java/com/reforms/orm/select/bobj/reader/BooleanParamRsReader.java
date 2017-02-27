package com.reforms.orm.select.bobj.reader;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.reforms.orm.select.SelectedColumn;

/**
 * Контракт на чтение значения Boolean из выборки ResultSet
 * @author evgenie
 */
class BooleanParamRsReader implements IParamRsReader<Boolean> {

    @Override
    public Boolean readValue(SelectedColumn column, ResultSet rs, Class<?> toBeClass) throws SQLException {
        boolean value = rs.getBoolean(column.getIndex());
        if (rs.wasNull()) {
            return null;
        }
        return value;
    }

}
