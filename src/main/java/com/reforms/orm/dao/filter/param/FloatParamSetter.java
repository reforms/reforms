package com.reforms.orm.dao.filter.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import com.reforms.ann.ThreadSafe;

/**
 * Установить параметр типа Float
 * @author evgenie
 */
@ThreadSafe
public class FloatParamSetter implements ParamSetter {

    @Override
    public void setValue(Object value, int index, PreparedStatement ps) throws SQLException {
        Float floatValue = convertValue(value);
        if (floatValue == null) {
            ps.setNull(index, Types.REAL);
        } else {
            ps.setFloat(index, floatValue);
        }
    }

    @Override
    public boolean acceptValue(Object value) {
        return convertValue(value) != null;
    }

    protected Float convertValue(Object value) {
        if (value instanceof Float) {
            return ((Float) value);
        }
        if (value instanceof Double) {
            return ((Double) value).floatValue();
        }
        return null;
    }
}
