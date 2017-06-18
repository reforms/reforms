package com.reforms.orm.dao.filter.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import com.reforms.ann.ThreadSafe;

/**
 * Установить параметр типа Double
 * @author evgenie
 */
@ThreadSafe
public class DoubleParamSetter implements ParamSetter {

    @Override
    public void setValue(Object value, int index, PreparedStatement ps) throws SQLException {
        Double doubleValue = convertValue(value);
        if (doubleValue == null) {
            ps.setNull(index, Types.DOUBLE);
        } else {
            ps.setDouble(index, doubleValue);
        }
    }

    @Override
    public boolean acceptValue(Object value) {
        return convertValue(value) != null;
    }

    protected Double convertValue(Object value) {
        if (value instanceof Double) {
            return ((Double) value);
        }
        return null;
    }
}
