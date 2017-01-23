package com.reforms.orm.filter.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Установить параметр типа Double
 */
public class DoubleParamSetter implements ParamSetter {

    @Override
    public void setValue(Object value, int index, PreparedStatement ps) throws SQLException {
        ps.setDouble(index, getDoubleValue(value));
    }

    protected double getDoubleValue(Object value) {
        if (value instanceof Double) {
            return ((Double) value).doubleValue();
        }
        throw new IllegalStateException("Невозможно преобразовать значение '" + value + "' к типу double");
    }
}
