package com.reforms.orm.filter.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Установить параметр типа Short
 *
 */
public class ShortParamSetter implements ParamSetter {

    @Override
    public void setValue(Object value, int index, PreparedStatement ps) throws SQLException {
        ps.setShort(index, getShortValue(value));
    }

    protected short getShortValue(Object value) {
        if (value instanceof Short) {
            return ((Short) value).shortValue();
        }
        if (value instanceof Integer) {
            return ((Integer) value).shortValue();
        }
        if (value instanceof Long) {
            return ((Long) value).shortValue();
        }
        throw new IllegalStateException("Невозможно преобразовать значение '" + value + "' к типу short");
    }
}
