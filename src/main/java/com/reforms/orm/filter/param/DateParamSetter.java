package com.reforms.orm.filter.param;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Установить параметр типа Date
 *
 */
public class DateParamSetter implements ParamSetter {

    @Override
    public void setValue(Object value, int index, PreparedStatement ps) throws SQLException {
        ps.setDate(index, getDateValue(value));
    }

    protected Date getDateValue(Object value) {
        if (value instanceof Date) {
            return (Date) value;
        }
        if (value instanceof java.util.Date) {
            return new Date(((java.util.Date) value).getTime());
        }
        throw new IllegalStateException("Невозможно преобразовать значение '" + value + "' к типу java.sql.Date");
    }
}
