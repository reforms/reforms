package com.reforms.orm;

import com.reforms.orm.filter.param.ParamSetterFactory;
import com.reforms.orm.reflex.ReflexorCache;
import com.reforms.orm.scheme.ISchemeManager;
import com.reforms.orm.select.bobj.IColumnToFieldNameConverter;
import com.reforms.orm.select.bobj.IResultSetValueAdapter;
import com.reforms.orm.select.bobj.reader.ParamRsReaderFactory;
import com.reforms.orm.select.report.IColumnToRecordNameConverter;
import com.reforms.orm.select.report.converter.ColumnValueConverterFactory;

public class OrmContext {

    private ColumnValueConverterFactory columnValueConverterFactory;

    private IColumnToRecordNameConverter columnToRecordNameConverter;

    private IConnectionHolder connectionHolder;

    private ParamSetterFactory paramSetterFactory;

    private ParamRsReaderFactory paramRsReaderFactory;

    private IResultSetValueAdapter resultSetValueAdapter;

    private IColumnToFieldNameConverter columnToFieldNameConverter;

    private ISchemeManager schemeManager;

    private ReflexorCache reflexorCache;

    public ColumnValueConverterFactory getColumnValueConverterFactory() {
        return columnValueConverterFactory;
    }

    public void setColumnValueConverterFactory(ColumnValueConverterFactory columnValueConverterFactory) {
        this.columnValueConverterFactory = columnValueConverterFactory;
    }

    public IColumnToRecordNameConverter getColumnToRecordNameConverter() {
        return columnToRecordNameConverter;
    }

    public void setColumnToRecordNameConverter(IColumnToRecordNameConverter columnToRecordNameConverter) {
        this.columnToRecordNameConverter = columnToRecordNameConverter;
    }

    public IConnectionHolder getConnectionHolder() {
        return connectionHolder;
    }

    public void setConnectionHolder(IConnectionHolder connectionHolder) {
        this.connectionHolder = connectionHolder;
    }

    public ParamSetterFactory getParamSetterFactory() {
        return paramSetterFactory;
    }

    public void setParamSetterFactory(ParamSetterFactory paramSetterFactory) {
        this.paramSetterFactory = paramSetterFactory;
    }

    public ParamRsReaderFactory getParamRsReaderFactory() {
        return paramRsReaderFactory;
    }

    public void setParamRsReaderFactory(ParamRsReaderFactory paramRsReaderFactory) {
        this.paramRsReaderFactory = paramRsReaderFactory;
    }

    public IResultSetValueAdapter getResultSetValueAdapter() {
        return resultSetValueAdapter;
    }

    public void setResultSetValueAdapter(IResultSetValueAdapter resultSetValueAdapter) {
        this.resultSetValueAdapter = resultSetValueAdapter;
    }

    public IColumnToFieldNameConverter getColumnToFieldNameConverter() {
        return columnToFieldNameConverter;
    }

    public void setColumnToFieldNameConverter(IColumnToFieldNameConverter columnToFieldNameConverter) {
        this.columnToFieldNameConverter = columnToFieldNameConverter;
    }

    public ReflexorCache getReflexorCache() {
        return reflexorCache;
    }

    public void setReflexorCache(ReflexorCache reflexorCache) {
        this.reflexorCache = reflexorCache;
    }

    public ISchemeManager getSchemeManager() {
        return schemeManager;
    }

    public void setSchemeManager(ISchemeManager schemeManager) {
        this.schemeManager = schemeManager;
    }
}
