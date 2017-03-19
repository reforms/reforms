package com.reforms.orm;

import com.reforms.orm.filter.param.ParamSetter;
import com.reforms.orm.scheme.ISchemeManager;
import com.reforms.orm.select.IResultSetReaderFactory;
import com.reforms.orm.select.bobj.IColumnToFieldNameConverter;
import com.reforms.orm.select.bobj.IResultSetValueAdapter;
import com.reforms.orm.select.bobj.reader.IResultSetValueReader;
import com.reforms.orm.select.report.IColumnToRecordNameConverter;
import com.reforms.orm.select.report.converter.IColumnValueConverter;

/**
 * Фасад, для взаимодействия с framework REFORMS
 * @author evgenie
 */
public interface IOrmContext {

    public void addReportValueConverter(String key, IColumnValueConverter converter);

    public void addParamReader(String key, IResultSetValueReader<?> converter);

    public void addParamSetter(String key, ParamSetter paramSetter);

    public void changeColumnToRecordNameConverter(CreateNewInstance<IColumnToRecordNameConverter> creator);

    public void setColumnToRecordNameConverter(IColumnToRecordNameConverter newColumnToRecordNameConverter);

    public void changeColumnToFieldNameConverter(CreateNewInstance<IColumnToFieldNameConverter> creator);

    public void setColumnToFieldNameConverter(IColumnToFieldNameConverter newColumnToFieldNameConverter);

    public void changeConnectionHolder(CreateNewInstance<IConnectionHolder> creator);

    public void setConnectionHolder(IConnectionHolder newConnectionHolder);

    public void changeResultSetValueAdapter(CreateNewInstance<IResultSetValueAdapter> creator);

    public void setResultSetValueAdapter(IResultSetValueAdapter newValueAdapter);

    public void changeSchemeManager(CreateNewInstance<ISchemeManager> creator);

    public void setSchemeManager(ISchemeManager newSchemeManager);

    public void changeResultSetReaderFactory(CreateNewInstance<IResultSetReaderFactory> creator);

    public void setResultSetReaderFactory(IResultSetReaderFactory resultSetReaderFactory);

    public void sealed();
}
