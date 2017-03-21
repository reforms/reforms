package com.reforms.orm.dao.bobj.reader;

import com.reforms.orm.dao.column.SelectedColumn;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Контракт на чтение значения BigInteger из выборки ResultSet
 * @author evgenie
 */
class BigIntegerResultSetValueReader implements IResultSetValueReader<BigInteger> {

    @Override
    public BigInteger readValue(SelectedColumn column, ResultSet rs, Class<?> toBeClass) throws SQLException {
        BigDecimal value = rs.getBigDecimal(column.getIndex());
        if (rs.wasNull()) {
            return null;
        }
        return value.toBigInteger();
    }

}
