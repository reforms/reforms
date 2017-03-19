package com.reforms.orm.select.report;

import com.reforms.orm.select.ColumnAlias;
import com.reforms.orm.select.IResultSetObjectReader;
import com.reforms.orm.select.SelectedColumn;
import com.reforms.orm.select.report.converter.ColumnValueConverterFactory;
import com.reforms.orm.select.report.converter.IColumnValueConverter;
import com.reforms.orm.select.report.model.ReportRecord;

import java.sql.ResultSet;
import java.util.List;

import static com.reforms.orm.OrmConfigurator.getInstance;

/**
 *
 * @author evgenie
 */
public class ResultSetRecordReader implements IResultSetObjectReader {

    private List<SelectedColumn> columns;
    private ColumnValueConverterFactory converterFactory;
    private IColumnToRecordNameConverter columnToRecordNameConverter;

    public ResultSetRecordReader(List<SelectedColumn> columns) {
        this.columns = columns;
        converterFactory = getInstance(ColumnValueConverterFactory.class);
        columnToRecordNameConverter = getInstance(IColumnToRecordNameConverter.class);
    }

    @Override
    public boolean canRead(ResultSet rs) throws Exception {
        return rs.next();
    }

    @SuppressWarnings("unchecked")
    @Override
    public ReportRecord read(ResultSet rs) throws Exception {
        ReportRecord reportRecord = new ReportRecord();
        for (SelectedColumn column : columns) {
            ColumnAlias cAlias = column.getColumnAlias();
            String aliasPrefix = cAlias.getAliasPrefix();
            IColumnValueConverter converter = converterFactory.getConverter(aliasPrefix);
            if (converter == null) {
                throw new IllegalStateException("Не определен преобразователь данных для типа '" + aliasPrefix + "'");
            }
            String value = converter.convertValue(column, rs);
            String key = columnToRecordNameConverter.getRecordName(column);
            reportRecord.put(key, value);
        }
        return reportRecord;
    }

}
