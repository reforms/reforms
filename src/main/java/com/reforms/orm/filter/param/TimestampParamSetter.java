package com.reforms.orm.filter.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.reforms.ann.ThreadSafe;

/**
 * Установить параметр типа Timestamp
 * @author evgenie
 */
@ThreadSafe
public class TimestampParamSetter implements ParamSetter {

    @Override
    public void setValue(Object value, int index, PreparedStatement ps) throws SQLException {
        ps.setTimestamp(index, getTimeValue(value));
    }

    @Override
    public boolean acceptValue(Object value) {
        return convertValue(value) != null;
    }

    protected Timestamp getTimeValue(Object value) {
        Timestamp timestampValue = convertValue(value);
        if (timestampValue == null) {
            throw new IllegalStateException("Невозможно преобразовать значение '" + value + "' к типу java.sql.Timestamp");
        }
        return timestampValue;
    }

    protected Timestamp convertValue(Object value) {
        if (value instanceof Timestamp) {
            return (Timestamp) value;
        }
        if (value instanceof java.util.Date) {
            return new Timestamp(((java.util.Date) value).getTime());
        }
        return null;
    }
}
