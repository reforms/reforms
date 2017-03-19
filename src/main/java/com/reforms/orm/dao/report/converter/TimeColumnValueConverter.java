package com.reforms.orm.dao.report.converter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.reforms.orm.dao.column.SelectedColumn;

/**
 * Контракт на преобразование значения из выборки ResultSet в строковое значение
 * @author evgenie
 */
class TimeColumnValueConverter implements IColumnValueConverter {

    private ThreadLocal<SimpleDateFormat> tlFormatter;

    TimeColumnValueConverter(ThreadLocal<SimpleDateFormat> tlFormatter) {
        this.tlFormatter = tlFormatter;
    }

    @Override
    public String convertValue(SelectedColumn column, ResultSet rs) throws SQLException {
        java.sql.Time value = rs.getTime(column.getIndex());
        if (rs.wasNull()) {
            return null;
        }
        java.util.Date uDate = new Date(value.getTime());
        SimpleDateFormat formatter = tlFormatter.get();
        return formatter.format(uDate);
    }

}
