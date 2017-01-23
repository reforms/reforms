package com.reforms.orm.select.bobj.reader;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Контракт на чтение значения Double из выборки ResultSet
 * @author evgenie
 */
class FloatParamRsReader implements IParamRsReader<Float> {

    @Override
    public Float readValue(int columnIndex, ResultSet rs) throws SQLException {
        float value = rs.getFloat(columnIndex);
        if (rs.wasNull()) {
            return null;
        }
        return value;
    }

}
