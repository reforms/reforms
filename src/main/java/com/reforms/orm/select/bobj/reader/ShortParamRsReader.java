package com.reforms.orm.select.bobj.reader;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.reforms.orm.select.SelectedColumn;

/**
 * Контракт на чтение значения Short из выборки ResultSet
 * @author evgenie
 */
class ShortParamRsReader implements IParamRsReader<Short> {

    @Override
    public Short readValue(SelectedColumn column, ResultSet rs, Class<?> toBeClass) throws SQLException {
        short value = rs.getShort(column.getIndex());
        if (rs.wasNull()) {
            return null;
        }
        return value;
    }

}
