package com.reforms.orm.select.bobj.reader;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Контракт на чтение значения Short из выборки ResultSet
 * @author evgenie
 */
class ShortParamRsReader implements IParamRsReader<Short> {

    @Override
    public Short readValue(int columnIndex, ResultSet rs) throws SQLException {
        short value = rs.getShort(columnIndex);
        if (rs.wasNull()) {
            return null;
        }
        return value;
    }

}
