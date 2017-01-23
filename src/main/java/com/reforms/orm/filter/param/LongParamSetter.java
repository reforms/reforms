package com.reforms.orm.filter.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Установить параметр типа Long
 *
 */
public class LongParamSetter implements ParamSetter {

    @Override
    public void setValue(Object value, int index, PreparedStatement ps) throws SQLException {
        ps.setLong(index, getLongValue(value));
    }

    protected long getLongValue(Object value) {
        if (value instanceof Long) {
            return ((Long) value).longValue();
        }
        throw new IllegalStateException("Невозможно преобразовать значение '" + value + "' к типу long");
    }
}
