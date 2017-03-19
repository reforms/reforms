package com.reforms.orm.select.bobj.reader;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.reforms.orm.select.SelectedColumn;

/**
 * Контракт на чтение значения Boolean из выборки ResultSet
 * @author evgenie
 */
class ByteResultSetValueReader implements IResultSetValueReader<Byte> {

    @Override
    public Byte readValue(SelectedColumn column, ResultSet rs, Class<?> toBeClass) throws SQLException {
        byte value = rs.getByte(column.getIndex());
        if (rs.wasNull()) {
            return null;
        }
        return value;
    }

}
