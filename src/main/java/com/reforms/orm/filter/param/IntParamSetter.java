package com.reforms.orm.filter.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.reforms.ann.ThreadSafe;

/**
 * Установить параметр типа Int
 * @author evgenie
 */
@ThreadSafe
public class IntParamSetter implements ParamSetter {

    @Override
    public void setValue(Object value, int index, PreparedStatement ps) throws SQLException {
        ps.setInt(index, getIntValue(value));
    }

    @Override
    public boolean acceptValue(Object value) {
        return convertValue(value) != null;
    }

    protected int getIntValue(Object value) {
        Integer integerValue = convertValue(value);
        if (integerValue == null) {
            throw new IllegalStateException("Невозможно преобразовать значение '" + value + "' к типу int");
        }
        return integerValue;
    }

    protected Integer convertValue(Object value) {
        if (value instanceof Integer) {
            return ((Integer) value);
        }
        if (value instanceof Long) {
            return ((Long) value).intValue();
        }
        return null;
    }
}
