package com.reforms.orm.select.bobj.reader;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Контракт на чтение значения BigDecimal из выборки ResultSet
 * @author evgenie
 */
class BigDecimalParamRsReader implements IParamRsReader<BigDecimal> {

    @Override
    public BigDecimal readValue(int columnIndex, ResultSet rs) throws SQLException {
        BigDecimal value = rs.getBigDecimal(columnIndex);
        if (rs.wasNull()) {
            return null;
        }
        return value;
    }

}
