package com.reforms.orm.filter.param;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Установить параметр типа BigDecimal
 *
 */
public class BigDecimalParamSetter implements ParamSetter {

    @Override
    public void setValue(Object value, int index, PreparedStatement ps) throws SQLException {
        ps.setBigDecimal(index, getBigDecimalValue(value));
    }

    protected BigDecimal getBigDecimalValue(Object value) {
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof BigInteger) {
            return new BigDecimal((BigInteger) value);
        }
        throw new IllegalStateException("Невозможно преобразовать значение '" + value + "' к типу java.math.BigDecimal");
    }
}
