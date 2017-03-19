package com.reforms.orm.dao.filter.param.setter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.reforms.ann.ThreadSafe;

/**
 * Установить параметр типа Double
 * @author evgenie
 */
@ThreadSafe
public class DoubleParamSetter implements ParamSetter {

    @Override
    public void setValue(Object value, int index, PreparedStatement ps) throws SQLException {
        ps.setDouble(index, getDoubleValue(value));
    }

    @Override
    public boolean acceptValue(Object value) {
        return convertValue(value) != null;
    }

    protected double getDoubleValue(Object value) {
        Double doubleValue = convertValue(value);
        if (doubleValue == null) {
            throw new IllegalStateException("Невозможно преобразовать значение '" + value + "' к типу double");
        }
        return doubleValue;
    }

    protected Double convertValue(Object value) {
        if (value instanceof Double) {
            return ((Double) value);
        }
        return null;
    }
}
