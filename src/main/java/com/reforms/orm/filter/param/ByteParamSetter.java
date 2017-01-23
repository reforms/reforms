package com.reforms.orm.filter.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Установить параметр типа Byte
 *
 */
public class ByteParamSetter implements ParamSetter {

    @Override
    public void setValue(Object value, int index, PreparedStatement ps) throws SQLException {
        ps.setByte(index, getByteValue(value));
    }

    protected byte getByteValue(Object value) {
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
        throw new IllegalStateException("Невозможно преобразовать значение '" + value + "' к типу byte");
    }
}
