package com.reforms.orm.filter.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.reforms.ann.ThreadSafe;

/**
 * Установить параметр типа Boolean
 * @author evgenie
 */
@ThreadSafe
public class BooleanParamSetter implements ParamSetter {

    @Override
    public void setValue(Object value, int index, PreparedStatement ps) throws SQLException {
        ps.setBoolean(index, getBooleanValue(value));
    }

    @Override
    public boolean acceptValue(Object value) {
        return convertValue(value) != null;
    }

    protected boolean getBooleanValue(Object value) {
        Boolean booleanValue = convertValue(value);
        if (booleanValue == null) {
            throw new IllegalStateException("Невозможно преобразовать значение '" + value + "' к типу boolean");
        }
        return booleanValue;
    }

    protected Boolean convertValue(Object value) {
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
        return null;
    }
}
