package com.reforms.orm;

import com.reforms.orm.dao.IResultSetReaderFactory;
import com.reforms.orm.dao.bobj.IColumnToFieldNameConverter;
import com.reforms.orm.dao.bobj.IResultSetValueAdapter;
import com.reforms.orm.dao.bobj.reader.IResultSetValueReader;
import com.reforms.orm.dao.bobj.reader.ResultSetValueReaderFactory;
import com.reforms.orm.dao.filter.param.setter.ParamSetter;
import com.reforms.orm.dao.filter.param.setter.ParamSetterFactory;
import com.reforms.orm.dao.report.IColumnToRecordNameConverter;
import com.reforms.orm.dao.report.converter.ColumnValueConverterFactory;
import com.reforms.orm.dao.report.converter.IColumnValueConverter;
import com.reforms.orm.scheme.ISchemeManager;

import static com.reforms.orm.OrmConfigurator.getInstance;
import static com.reforms.orm.OrmConfigurator.putInstance;

/**
 * Реализация контракта на изменение апи по работе с REFORMS
 * @author evgenie
 */
class OrmContext implements IOrmContext {

    @Override
    public void addReportValueConverter(String key, IColumnValueConverter converter) {
        ColumnValueConverterFactory factory = getInstance(ColumnValueConverterFactory.class);
        factory.addCustomConverter(key, converter);
    }

    @Override
    public void addParamReader(String key, IResultSetValueReader<?> converter) {
        ResultSetValueReaderFactory factory = getInstance(ResultSetValueReaderFactory.class);
        factory.addCustomParamReader(converter, key);
    }

    @Override
    public void addParamSetter(String key, ParamSetter paramSetter) {
        ParamSetterFactory factory = getInstance(ParamSetterFactory.class);
        factory.addCustomParamSetter(key, paramSetter);
    }

    @Override
    public void changeColumnToRecordNameConverter(CreateNewInstance<IColumnToRecordNameConverter> recordNameConverterCreator) {
        IColumnToRecordNameConverter currentRecordNameConverter = getInstance(IColumnToRecordNameConverter.class);
        IColumnToRecordNameConverter newColumnToRecordNameConverter = recordNameConverterCreator.createNew(currentRecordNameConverter);
        if (newColumnToRecordNameConverter != null) {
            putInstance(IColumnToRecordNameConverter.class, newColumnToRecordNameConverter);
        }
    }

    @Override
    public void setColumnToRecordNameConverter(IColumnToRecordNameConverter newColumnToRecordNameConverter) {
        putInstance(IColumnToRecordNameConverter.class, newColumnToRecordNameConverter);
    }

    @Override
    public void changeColumnToFieldNameConverter(CreateNewInstance<IColumnToFieldNameConverter> columnNameConverterCreator) {
        IColumnToFieldNameConverter currentColumnNameConverter = getInstance(IColumnToFieldNameConverter.class);
        IColumnToFieldNameConverter newColumnToFieldNameConverter = columnNameConverterCreator.createNew(currentColumnNameConverter);
        if (newColumnToFieldNameConverter != null) {
            putInstance(IColumnToFieldNameConverter.class, newColumnToFieldNameConverter);
        }
    }

    @Override
    public void setColumnToFieldNameConverter(IColumnToFieldNameConverter newColumnToFieldNameConverter) {
        putInstance(IColumnToFieldNameConverter.class, newColumnToFieldNameConverter);
    }

    @Override
    public void changeConnectionHolder(CreateNewInstance<IConnectionHolder> connectionHolderCreator) {
        IConnectionHolder currentConnectionHolder = getInstance(IConnectionHolder.class);
        IConnectionHolder newConnectionHolder = connectionHolderCreator.createNew(currentConnectionHolder);
        if (newConnectionHolder != null) {
            putInstance(IConnectionHolder.class, newConnectionHolder);
        }
    }

    @Override
    public void setConnectionHolder(IConnectionHolder newConnectionHolder) {
        putInstance(IConnectionHolder.class, newConnectionHolder);
    }

    @Override
    public void changeResultSetValueAdapter(CreateNewInstance<IResultSetValueAdapter> adapterCreator) {
        IResultSetValueAdapter currentValueAdapter = getInstance(IResultSetValueAdapter.class);
        IResultSetValueAdapter newValueAdapter = adapterCreator.createNew(currentValueAdapter);
        if (newValueAdapter != null) {
            putInstance(IResultSetValueAdapter.class, newValueAdapter);
        }
    }

    @Override
    public void setResultSetValueAdapter(IResultSetValueAdapter newValueAdapter) {
        putInstance(IResultSetValueAdapter.class, newValueAdapter);
    }

    @Override
    public void changeSchemeManager(CreateNewInstance<ISchemeManager> schemeManagerCreator) {
        ISchemeManager currentSchemeManager = getInstance(ISchemeManager.class);
        ISchemeManager newSchemeManager = schemeManagerCreator.createNew(currentSchemeManager);
        if (newSchemeManager != null) {
            putInstance(ISchemeManager.class, newSchemeManager);
        }
    }

    @Override
    public void setSchemeManager(ISchemeManager newSchemeManager) {
        putInstance(ISchemeManager.class, newSchemeManager);
    }

    @Override
    public void changeResultSetReaderFactory(CreateNewInstance<IResultSetReaderFactory> resulSetReaderFactoryCreator) {
        IResultSetReaderFactory currentResulSetReaderFactory = getInstance(IResultSetReaderFactory.class);
        IResultSetReaderFactory newResulSetReaderFactoryCreator = resulSetReaderFactoryCreator.createNew(currentResulSetReaderFactory);
        if (newResulSetReaderFactoryCreator != null) {
            putInstance(IResultSetReaderFactory.class, newResulSetReaderFactoryCreator);
        }
    }

    @Override
    public void setResultSetReaderFactory(IResultSetReaderFactory resultSetReaderFactory) {
        putInstance(IResultSetReaderFactory.class, resultSetReaderFactory);
    }

    @Override
    public void sealed() {
        ColumnValueConverterFactory columnValueFactory = getInstance(ColumnValueConverterFactory.class);
        columnValueFactory.sealedCustom();
        ResultSetValueReaderFactory resultSetValueReaderFactory = getInstance(ResultSetValueReaderFactory.class);
        resultSetValueReaderFactory.sealedCustom();
        ParamSetterFactory paramSetterfactory = getInstance(ParamSetterFactory.class);
        paramSetterfactory.sealedCustom();
    }
}
