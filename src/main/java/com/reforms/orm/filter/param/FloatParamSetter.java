package com.reforms.orm.filter.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Установить параметр типа Float
 */
public class FloatParamSetter implements ParamSetter {

    @Override
    public void setValue(Object value, int index, PreparedStatement ps) throws SQLException {
        ps.setFloat(index, getFloatValue(value));
    }

    protected float getFloatValue(Object value) {
        if (value instanceof Float) {
            return ((Float) value).floatValue();
        }
        if (value instanceof Double) {
            return ((Double) value).floatValue();
        }
        throw new IllegalStateException("Невозможно преобразовать значение '" + value + "' к типу float");
    }
}
