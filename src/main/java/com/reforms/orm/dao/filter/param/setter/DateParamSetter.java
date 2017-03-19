package com.reforms.orm.dao.filter.param.setter;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.reforms.ann.ThreadSafe;

/**
 * Установить параметр типа Date
 * @author evgenie
 */
@ThreadSafe
public class DateParamSetter implements ParamSetter {

    @Override
    public void setValue(Object value, int index, PreparedStatement ps) throws SQLException {
        ps.setDate(index, getDateValue(value));
    }

    @Override
    public boolean acceptValue(Object value) {
        return convertValue(value) != null;
    }

    protected Date getDateValue(Object value) {
        Date dataValue = convertValue(value);
        if (dataValue == null) {
            throw new IllegalStateException("Невозможно преобразовать значение '" + value + "' к типу java.sql.Date");
        }
        return dataValue;
    }

    protected Date convertValue(Object value) {
        if (value instanceof Date) {
            return (Date) value;
        }
        if (value instanceof java.util.Date) {
            return new Date(((java.util.Date) value).getTime());
        }
        return null;
    }
}
