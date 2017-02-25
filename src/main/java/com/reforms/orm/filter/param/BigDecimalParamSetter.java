package com.reforms.orm.filter.param;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.reforms.ann.ThreadSafe;

/**
 * Установить параметр типа BigDecimal
 * @author evgenie
 */
@ThreadSafe
public class BigDecimalParamSetter implements ParamSetter {

    @Override
    public void setValue(Object value, int index, PreparedStatement ps) throws SQLException {
        ps.setBigDecimal(index, getBigDecimalValue(value));
    }

    @Override
    public boolean acceptValue(Object value) {
        return convertValue(value) != null;
    }

    protected BigDecimal getBigDecimalValue(Object value) {
        BigDecimal bigDecimalValue = convertValue(value);
        if (bigDecimalValue == null) {
            throw new IllegalStateException("Невозможно преобразовать значение '" + value + "' к типу java.math.BigDecimal");
        }
        return bigDecimalValue;
    }

    protected BigDecimal convertValue(Object value) {
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof BigInteger) {
            return new BigDecimal((BigInteger) value);
        }
        return null;
    }
}
