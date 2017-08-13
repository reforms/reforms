package com.reforms.orm.dao.filter.param;

import com.reforms.ann.ThreadSafe;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Установить параметр типа Character
 * @author evgenie
 */
@ThreadSafe
public class CharParamSetter implements ParamSetter {

    @Override
    public void setValue(Object value, int index, PreparedStatement ps) throws SQLException {
        ps.setString(index, convertValue(value));
    }

    @Override
    public boolean acceptValue(Object value) {
        return convertValue(value) != null;
    }

    protected String convertValue(Object value) {
        if (value instanceof Character) {
            return value.toString();
        }
        if (value instanceof String) {
            return (String) value;
        }
        return null;
    }
}
