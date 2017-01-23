package com.reforms.orm.filter.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Установить параметр типа Timestamp
 *
 */
public class TimestampParamSetter implements ParamSetter {

    @Override
    public void setValue(Object value, int index, PreparedStatement ps) throws SQLException {
        ps.setTimestamp(index, getTimeValue(value));
    }

    protected Timestamp getTimeValue(Object value) {
        if (value instanceof Timestamp) {
            return (Timestamp) value;
        }
        if (value instanceof java.util.Date) {
            return new Timestamp(((java.util.Date) value).getTime());
        }
        throw new IllegalStateException("Невозможно преобразовать значение '" + value + "' к типу java.sql.Timestamp");
    }
}
