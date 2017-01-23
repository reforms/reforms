package com.reforms.orm.filter.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Установить параметр типа Boolean
 *
 */
public class BooleanParamSetter implements ParamSetter {

    @Override
    public void setValue(Object value, int index, PreparedStatement ps) throws SQLException {
        ps.setBoolean(index, getBooleanValue(value));
    }

    protected boolean getBooleanValue(Object value) {
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof Byte) {
            return ((Byte) value).byteValue() == 1;
        }
        if (value instanceof Short) {
            return ((Short) value).shortValue() == 1;
        }
        if (value instanceof Integer) {
            return ((Integer) value).intValue() == 1;
        }
        if (value instanceof Long) {
            return ((Long) value).longValue() == 1;
        }
        throw new IllegalStateException("Невозможно преобразовать значение '" + value + "' к типу boolean");
    }
}
