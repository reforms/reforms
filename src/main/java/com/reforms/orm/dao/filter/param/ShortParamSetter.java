package com.reforms.orm.dao.filter.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import com.reforms.ann.ThreadSafe;

/**
 * Установить параметр типа Short
 * @author evgenie
 */
@ThreadSafe
public class ShortParamSetter implements ParamSetter {

    @Override
    public void setValue(Object value, int index, PreparedStatement ps) throws SQLException {
        Short shortValue = convertValue(value);
        if (shortValue == null) {
            ps.setNull(index, Types.SMALLINT);
        } else {
            ps.setShort(index, shortValue);
        }
    }

    @Override
    public boolean acceptValue(Object value) {
        return convertValue(value) != null;
    }

    protected Short convertValue(Object value) {
        if (value instanceof Short) {
            return ((Short) value);
        }
        if (value instanceof Integer) {
            return ((Integer) value).shortValue();
        }
        if (value instanceof Long) {
            return ((Long) value).shortValue();
        }
        return null;
    }
}
