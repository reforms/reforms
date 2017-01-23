package com.reforms.orm.select.bobj.reader;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Контракт на чтение значения Boolean из выборки ResultSet
 * @author evgenie
 */
class ByteParamRsReader implements IParamRsReader<Byte> {

    @Override
    public Byte readValue(int columnIndex, ResultSet rs) throws SQLException {
        byte value = rs.getByte(columnIndex);
        if (rs.wasNull()) {
            return null;
        }
        return value;
    }

}
