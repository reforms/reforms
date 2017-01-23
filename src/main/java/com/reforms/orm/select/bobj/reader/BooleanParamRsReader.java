package com.reforms.orm.select.bobj.reader;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Контракт на чтение значения Boolean из выборки ResultSet
 * @author evgenie
 */
class BooleanParamRsReader implements IParamRsReader<Boolean> {

    @Override
    public Boolean readValue(int columnIndex, ResultSet rs) throws SQLException {
        boolean value = rs.getBoolean(columnIndex);
        if (rs.wasNull()) {
            return null;
        }
        return value;
    }

}
