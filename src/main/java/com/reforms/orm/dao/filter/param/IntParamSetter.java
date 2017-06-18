package com.reforms.orm.dao.filter.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import com.reforms.ann.ThreadSafe;

/**
 * Установить параметр типа Int
 * @author evgenie
 */
@ThreadSafe
public class IntParamSetter implements ParamSetter {

    @Override
    public void setValue(Object value, int index, PreparedStatement ps) throws SQLException {
        Integer intValue = convertValue(value);
        if (intValue == null) {
            ps.setNull(index, Types.INTEGER);
        } else {
            ps.setInt(index, intValue);
        }
    }

    @Override
    public boolean acceptValue(Object value) {
        return convertValue(value) != null;
    }

    protected Integer convertValue(Object value) {
        if (value instanceof Integer) {
            return ((Integer) value);
        }
        if (value instanceof Long) {
            return ((Long) value).intValue();
        }
        return null;
    }
}
