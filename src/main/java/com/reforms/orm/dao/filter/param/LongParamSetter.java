package com.reforms.orm.dao.filter.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import com.reforms.ann.ThreadSafe;

/**
 * Установить параметр типа Long
 * @author evgenie
 */
@ThreadSafe
public class LongParamSetter implements ParamSetter {

    @Override
    public void setValue(Object value, int index, PreparedStatement ps) throws SQLException {
        Long longValue = convertValue(value);
        if (longValue == null) {
            ps.setNull(index, Types.BIGINT);
        } else {
            ps.setLong(index, longValue);
        }
    }

    @Override
    public boolean acceptValue(Object value) {
        return convertValue(value) != null;
    }

    protected Long convertValue(Object value) {
        if (value instanceof Long) {
            return ((Long) value);
        }
        return null;
    }
}
