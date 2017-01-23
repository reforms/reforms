package com.reforms.orm.filter.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Установить параметр типа String
 *
 */
public class StringParamSetter implements ParamSetter {

    @Override
    public void setValue(Object value, int index, PreparedStatement ps) throws SQLException {
        ps.setString(index, getStringValue(value));
    }

    protected String getStringValue(Object value) {
        if (value instanceof String) {
            return (String) value;
        }
        throw new IllegalStateException("Невозможно преобразовать значение '" + value + "' к типу java.lang.String");
    }
}
