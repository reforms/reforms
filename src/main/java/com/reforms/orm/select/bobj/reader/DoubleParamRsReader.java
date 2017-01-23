package com.reforms.orm.select.bobj.reader;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Контракт на чтение значения Double из выборки ResultSet
 * @author evgenie
 */
class DoubleParamRsReader implements IParamRsReader<Double> {

    @Override
    public Double readValue(int columnIndex, ResultSet rs) throws SQLException {
        double value = rs.getDouble(columnIndex);
        if (rs.wasNull()) {
            return null;
        }
        return value;
    }

}
