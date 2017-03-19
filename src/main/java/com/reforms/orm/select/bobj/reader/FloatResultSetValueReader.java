package com.reforms.orm.select.bobj.reader;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.reforms.orm.select.SelectedColumn;

/**
 * Контракт на чтение значения Double из выборки ResultSet
 * @author evgenie
 */
class FloatResultSetValueReader implements IResultSetValueReader<Float> {

    @Override
    public Float readValue(SelectedColumn column, ResultSet rs, Class<?> toBeClass) throws SQLException {
        float value = rs.getFloat(column.getIndex());
        if (rs.wasNull()) {
            return null;
        }
        return value;
    }

}
