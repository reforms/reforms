package com.reforms.orm.dao.report.converter;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;

import com.reforms.orm.dao.column.SelectedColumn;

/**
 * Контракт на преобразование значения из выборки ResultSet в строковое значение
 * @author evgenie
 */
class BigDecimalColumnValueConverter implements IColumnValueConverter {

    private ThreadLocal<DecimalFormat> tlFormatter;

    BigDecimalColumnValueConverter(ThreadLocal<DecimalFormat> tlFormatter) {
        this.tlFormatter = tlFormatter;
    }

    @Override
    public String convertValue(SelectedColumn column, ResultSet rs) throws SQLException {
        BigDecimal value = rs.getBigDecimal(column.getIndex());
        if (rs.wasNull()) {
            return null;
        }
        DecimalFormat formatter = tlFormatter.get();
        return formatter.format(value);
    }

}
