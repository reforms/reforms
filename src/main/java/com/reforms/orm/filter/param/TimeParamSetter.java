package com.reforms.orm.filter.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;

import com.reforms.ann.ThreadSafe;

/**
 * Установить параметр типа Time
 * @author evgenie
 */
@ThreadSafe
public class TimeParamSetter implements ParamSetter {

    @Override
    public void setValue(Object value, int index, PreparedStatement ps) throws SQLException {
        ps.setTime(index, getTimeValue(value));
    }

    @Override
    public boolean acceptValue(Object value) {
        return convertValue(value) != null;
    }

    protected Time getTimeValue(Object value) {
        Time timeValue = convertValue(value);
        if (timeValue == null) {
            throw new IllegalStateException("Невозможно преобразовать значение '" + value + "' к типу java.sql.Time");
        }
        return timeValue;
    }

    protected Time convertValue(Object value) {
        if (value instanceof Time) {
            return (Time) value;
        }
        if (value instanceof java.util.Date) {
            return new Time(((java.util.Date) value).getTime());
        }
        return null;
    }
}
