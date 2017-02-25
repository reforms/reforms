package com.reforms.orm.filter.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.reforms.ann.ThreadSafe;

/**
 * Установить параметр типа Byte
 * @author evgenie
 */
@ThreadSafe
public class ByteParamSetter implements ParamSetter {

    @Override
    public void setValue(Object value, int index, PreparedStatement ps) throws SQLException {
        ps.setByte(index, getByteValue(value));
    }

    @Override
    public boolean acceptValue(Object value) {
        return convertValue(value) != null;
    }

    protected byte getByteValue(Object value) {
        Byte byteValue = convertValue(value);
        if (byteValue == null) {
            throw new IllegalStateException("Невозможно преобразовать значение '" + value + "' к типу byte");
        }
        return byteValue;
    }

    protected Byte convertValue(Object value) {
        if (value instanceof Byte) {
            return ((Byte) value);
        }
        if (value instanceof Short) {
            return ((Short) value).byteValue();
        }
        if (value instanceof Integer) {
            return ((Integer) value).byteValue();
        }
        if (value instanceof Long) {
            return ((Long) value).byteValue();
        }
        return null;
    }
}
