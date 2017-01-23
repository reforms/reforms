package com.reforms.orm.filter.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;

/**
 * Установить параметр типа Time
 *
 */
public class TimeParamSetter implements ParamSetter {

    @Override
    public void setValue(Object value, int index, PreparedStatement ps) throws SQLException {
        ps.setTime(index, getTimeValue(value));
    }

    protected Time getTimeValue(Object value) {
        if (value instanceof Time) {
            return (Time) value;
        }
        if (value instanceof java.util.Date) {
            return new Time(((java.util.Date) value).getTime());
        }
        throw new IllegalStateException("Невозможно преобразовать значение '" + value + "' к типу java.sql.Time");
    }
}
