package com.reforms.orm.select.bobj.reader;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Контракт на чтение значения Timestamp из выборки ResultSet
 * @author evgenie
 */
class TimestampParamRsReader implements IParamRsReader<Timestamp> {

    @Override
    public Timestamp readValue(int columnIndex, ResultSet rs) throws SQLException {
        java.sql.Timestamp value = rs.getTimestamp(columnIndex);
        if (rs.wasNull()) {
            return null;
        }
        return value;
    }

}
