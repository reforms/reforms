package com.reforms.orm.select.bobj.reader;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Контракт на чтение значения Long из выборки ResultSet
 * @author evgenie
 */
class LongParamRsReader implements IParamRsReader<Long> {

    @Override
    public Long readValue(int columnIndex, ResultSet rs) throws SQLException {
        long value = rs.getLong(columnIndex);
        if (rs.wasNull()) {
            return null;
        }
        return value;
    }

}
