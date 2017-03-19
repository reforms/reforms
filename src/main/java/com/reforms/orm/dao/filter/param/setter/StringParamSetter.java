package com.reforms.orm.dao.filter.param.setter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.reforms.ann.ThreadSafe;

/**
 * Установить параметр типа String
 * @author evgenie
 */
@ThreadSafe
public class StringParamSetter implements ParamSetter {

    @Override
    public void setValue(Object value, int index, PreparedStatement ps) throws SQLException {
        ps.setString(index, getStringValue(value));
    }

    @Override
    public boolean acceptValue(Object value) {
        return convertValue(value) != null;
    }

    protected String getStringValue(Object value) {
        String stringValue = convertValue(value);
        if (stringValue == null) {
            throw new IllegalStateException("Невозможно преобразовать значение '" + value + "' к типу java.lang.String");
        }
        return stringValue;
    }

    protected String convertValue(Object value) {
        if (value instanceof String) {
            return (String) value;
        }
        return null;
    }
}
