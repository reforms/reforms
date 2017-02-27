package com.reforms.orm.select.bobj.reader;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.reforms.orm.select.SelectedColumn;

/**
 * Контракт на чтение значения BigDecimal из выборки ResultSet
 * @author evgenie
 */
class BigDecimalParamRsReader implements IParamRsReader<BigDecimal> {

    @Override
    public BigDecimal readValue(SelectedColumn column, ResultSet rs, Class<?> toBeClass) throws SQLException {
        BigDecimal value = rs.getBigDecimal(column.getIndex());
        if (rs.wasNull()) {
            return null;
        }
        return value;
    }

}
