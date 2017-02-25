package com.reforms.orm.filter.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.reforms.ann.ThreadSafe;

/**
 * Установить параметр типа Long
 * @author evgenie
 */
@ThreadSafe
public class LongParamSetter implements ParamSetter {

    @Override
    public void setValue(Object value, int index, PreparedStatement ps) throws SQLException {
        ps.setLong(index, getLongValue(value));
    }

    @Override
    public boolean acceptValue(Object value) {
        return convertValue(value) != null;
    }

    protected long getLongValue(Object value) {
        Long longValue = convertValue(value);
        if (longValue == null) {
            throw new IllegalStateException("Невозможно преобразовать значение '" + value + "' к типу long");
        }
        return longValue;
    }

    protected Long convertValue(Object value) {
        if (value instanceof Long) {
            return ((Long) value);
        }
        return null;
    }
}
