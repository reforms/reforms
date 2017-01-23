package com.reforms.orm.select.bobj.reader;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Контракт на чтение значения Integer из выборки ResultSet
 * @author evgenie
 */
class IntParamRsReader implements IParamRsReader<Integer> {

    @Override
    public Integer readValue(int columnIndex, ResultSet rs) throws SQLException {
        int value = rs.getInt(columnIndex);
        if (rs.wasNull()) {
            return null;
        }
        return value;
    }

}
