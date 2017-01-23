package com.reforms.orm.select.bobj.reader;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Контракт на чтение значения String из выборки ResultSet
 * @author evgenie
 */
class StringParamRsReader implements IParamRsReader<String> {

    @Override
    public String readValue(int columnIndex, ResultSet rs) throws SQLException {
        String value = rs.getString(columnIndex);
        if (rs.wasNull()) {
            return null;
        }
        return value;
    }

}
