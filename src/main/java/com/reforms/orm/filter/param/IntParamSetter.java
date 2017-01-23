package com.reforms.orm.filter.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Установить параметр типа Int
 *
 */
public class IntParamSetter implements ParamSetter {

    @Override
    public void setValue(Object value, int index, PreparedStatement ps) throws SQLException {
        ps.setInt(index, getIntValue(value));
    }

    protected int getIntValue(Object value) {
        if (value instanceof Integer) {
            return ((Integer) value).intValue();
        }
        if (value instanceof Long) {
            return ((Long) value).intValue();
        }
        throw new IllegalStateException("Невозможно преобразовать значение '" + value + "' к типу int");
    }
}
