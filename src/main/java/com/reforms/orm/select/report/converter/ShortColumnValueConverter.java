package com.reforms.orm.select.report.converter;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.reforms.orm.select.SelectedColumn;

/**
 * Контракт на преобразование значения из выборки ResultSet в строковое значение
 * @author evgenie
 */
class ShortColumnValueConverter implements IColumnValueConverter {

    @Override
    public String convertValue(SelectedColumn column, ResultSet rs) throws SQLException {
        short value = rs.getShort(column.getIndex());
        if (rs.wasNull()) {
            return null;
        }
        return String.valueOf(value);
    }

}
