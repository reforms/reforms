package com.reforms.orm.select.bobj.reader;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;

/**
 * Контракт на чтение значения Time из выборки ResultSet
 * @author evgenie
 */
class TimeColumnValueConverter implements IParamRsReader<Time> {

    @Override
    public Time readValue(int columnIndex, ResultSet rs) throws SQLException {
        java.sql.Time value = rs.getTime(columnIndex);
        if (rs.wasNull()) {
            return null;
        }
        return value;
    }

}
