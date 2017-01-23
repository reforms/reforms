package com.reforms.orm.select.bobj.reader;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Контракт на чтение значения Date из выборки ResultSet
 * @author evgenie
 */
class DateParamRsReader implements IParamRsReader<Date> {

    @Override
    public Date readValue(int columnIndex, ResultSet rs) throws SQLException {
        java.sql.Date value = rs.getDate(columnIndex);
        if (rs.wasNull()) {
            return null;
        }
        return value;
    }

}
