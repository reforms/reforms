package com.reforms.orm.filter.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.reforms.ann.ThreadSafe;

/**
 * Установить параметр типа Short
 * @author evgenie
 */
@ThreadSafe
public class ShortParamSetter implements ParamSetter {

    @Override
    public void setValue(Object value, int index, PreparedStatement ps) throws SQLException {
        ps.setShort(index, getShortValue(value));
    }

    @Override
    public boolean acceptValue(Object value) {
        return convertValue(value) != null;
    }

    protected short getShortValue(Object value) {
        Short shortValue = convertValue(value);
        if (shortValue == null) {
            throw new IllegalStateException("Невозможно преобразовать значение '" + value + "' к типу short");
        }
        return shortValue;
    }

    protected Short convertValue(Object value) {
        if (value instanceof Short) {
            return ((Short) value);
        }
        if (value instanceof Integer) {
            return ((Integer) value).shortValue();
        }
        if (value instanceof Long) {
            return ((Long) value).shortValue();
        }
        return null;
    }
}
