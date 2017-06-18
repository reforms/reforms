package com.reforms.orm.dao.filter.param;

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
        ps.setTime(index, convertValue(value));
    }

    @Override
    public boolean acceptValue(Object value) {
        return convertValue(value) != null;
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
