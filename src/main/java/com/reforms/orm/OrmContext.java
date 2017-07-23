package com.reforms.orm;

import com.reforms.orm.dao.IParamNameConverter;
import com.reforms.orm.dao.IResultSetReaderFactory;
import com.reforms.orm.dao.IJavaToSqlTypeResolver;
import com.reforms.orm.dao.bobj.IColumnToFieldNameConverter;
import com.reforms.orm.dao.bobj.IResultSetValueAdapter;
import com.reforms.orm.dao.bobj.reader.IResultSetValueReader;
import com.reforms.orm.dao.bobj.reader.ResultSetValueReaderFactory;
import com.reforms.orm.dao.filter.param.ParamSetter;
import com.reforms.orm.dao.filter.param.ParamSetterFactory;
import com.reforms.orm.dao.proxy.IMethodInterceptor;
import com.reforms.orm.dao.report.IColumnToRecordNameConverter;
import com.reforms.orm.dao.report.converter.ColumnValueConverterFactory;
import com.reforms.orm.dao.report.converter.IColumnValueConverter;
import com.reforms.orm.scheme.ISchemeManager;
import com.reforms.orm.scheme.SchemeManager;
import com.reforms.sql.db.DbType;

import static com.reforms.orm.OrmConfigurator.getInstance;
import static com.reforms.orm.OrmConfigurator.putInstance;

/**
 * Реализация контракта на изменение апи по работе с REFORMS
 * @author evgenie
 */
class OrmContext implements IOrmContext {

    private volatile boolean sealed = false;

    @Override
    public void addReportValueConverter(String key, IColumnValueConverter converter) {
        checkBeforeModify();
        ColumnValueConverterFactory factory = getInstance(ColumnValueConverterFactory.class);
        factory.addCustomConverter(key, converter);
    }

    @Override
    public void addParamReader(String key, IResultSetValueReader<?> converter) {
        checkBeforeModify();
        ResultSetValueReaderFactory factory = getInstance(ResultSetValueReaderFactory.class);
        factory.addCustomParamReader(converter, key);
    }

    @Override
    public void addParamSetter(String key, ParamSetter paramSetter) {
        checkBeforeModify();
        ParamSetterFactory factory = getInstance(ParamSetterFactory.class);
        factory.addCustomParamSetter(key, paramSetter);
    }

    @Override
    public IColumnToRecordNameConverter changeColumnToRecordNameConverter(CreateNewInstance<IColumnToRecordNameConverter> recordNameConverterCreator) {
        checkBeforeModify();
        IColumnToRecordNameConverter currentRecordNameConverter = getInstance(IColumnToRecordNameConverter.class);
        IColumnToRecordNameConverter newColumnToRecordNameConverter = recordNameConverterCreator.createNew(currentRecordNameConverter);
        if (newColumnToRecordNameConverter != null) {
            putInstance(IColumnToRecordNameConverter.class, newColumnToRecordNameConverter);
            return currentRecordNameConverter;
        }
        return null;
    }

    @Override
    public void setColumnToRecordNameConverter(IColumnToRecordNameConverter newColumnToRecordNameConverter) {
        checkBeforeModify();
        putInstance(IColumnToRecordNameConverter.class, newColumnToRecordNameConverter);
    }

    @Override
    public IColumnToFieldNameConverter changeColumnToFieldNameConverter(CreateNewInstance<IColumnToFieldNameConverter> columnNameConverterCreator) {
        checkBeforeModify();
        IColumnToFieldNameConverter currentColumnNameConverter = getInstance(IColumnToFieldNameConverter.class);
        IColumnToFieldNameConverter newColumnToFieldNameConverter = columnNameConverterCreator.createNew(currentColumnNameConverter);
        if (newColumnToFieldNameConverter != null) {
            putInstance(IColumnToFieldNameConverter.class, newColumnToFieldNameConverter);
            return currentColumnNameConverter;
        }
        return null;
    }

    @Override
    public void setColumnToFieldNameConverter(IColumnToFieldNameConverter newColumnToFieldNameConverter) {
        checkBeforeModify();
        putInstance(IColumnToFieldNameConverter.class, newColumnToFieldNameConverter);
    }

    @Override
    public IParamNameConverter changeParamNameConverter(CreateNewInstance<IParamNameConverter> creator) {
        checkBeforeModify();
        IParamNameConverter currentParamNameConverter = getInstance(IParamNameConverter.class);
        IParamNameConverter newParamNameConverter = creator.createNew(currentParamNameConverter);
        if (newParamNameConverter != null) {
            putInstance(IParamNameConverter.class, newParamNameConverter);
            return currentParamNameConverter;
        }
        return null;
    }

    @Override
    public void setParamNameConverter(IParamNameConverter newParamNameConverter) {
        checkBeforeModify();
        putInstance(IParamNameConverter.class, newParamNameConverter);
    }

    @Override
    public IConnectionHolder changeConnectionHolder(CreateNewInstance<IConnectionHolder> connectionHolderCreator) {
        checkBeforeModify();
        IConnectionHolder currentConnectionHolder = getInstance(IConnectionHolder.class);
        IConnectionHolder newConnectionHolder = connectionHolderCreator.createNew(currentConnectionHolder);
        if (newConnectionHolder != null) {
            putInstance(IConnectionHolder.class, newConnectionHolder);
            return currentConnectionHolder;
        }
        return null;
    }

    @Override
    public void setConnectionHolder(IConnectionHolder newConnectionHolder) {
        checkBeforeModify();
        putInstance(IConnectionHolder.class, newConnectionHolder);
    }

    @Override
    public IResultSetValueAdapter changeResultSetValueAdapter(CreateNewInstance<IResultSetValueAdapter> adapterCreator) {
        checkBeforeModify();
        IResultSetValueAdapter currentValueAdapter = getInstance(IResultSetValueAdapter.class);
        IResultSetValueAdapter newValueAdapter = adapterCreator.createNew(currentValueAdapter);
        if (newValueAdapter != null) {
            putInstance(IResultSetValueAdapter.class, newValueAdapter);
            return currentValueAdapter;
        }
        return null;
    }

    @Override
    public void setResultSetValueAdapter(IResultSetValueAdapter newValueAdapter) {
        checkBeforeModify();
        putInstance(IResultSetValueAdapter.class, newValueAdapter);
    }

    @Override
    public void setDefaultScheme(String schemeName) {
        checkBeforeModify();
        ISchemeManager currentSchemeManager = getInstance(ISchemeManager.class);
        if (!(currentSchemeManager instanceof SchemeManager)) {
            throw new IllegalStateException("Не известная реализация ISchemeManager: '" + currentSchemeManager + "'");
        }
        ((SchemeManager) currentSchemeManager).setDefaultSchemeName(schemeName);
    }

    @Override
    public void setDefaultDbType(DbType dbType) {
        checkBeforeModify();
        ISchemeManager currentSchemeManager = getInstance(ISchemeManager.class);
        if (!(currentSchemeManager instanceof SchemeManager)) {
            throw new IllegalStateException("Не известная реализация ISchemeManager: '" + currentSchemeManager + "'");
        }
        ((SchemeManager) currentSchemeManager).setDefaultDbType(dbType);
    }

    @Override
    public ISchemeManager changeSchemeManager(CreateNewInstance<ISchemeManager> schemeManagerCreator) {
        checkBeforeModify();
        ISchemeManager currentSchemeManager = getInstance(ISchemeManager.class);
        ISchemeManager newSchemeManager = schemeManagerCreator.createNew(currentSchemeManager);
        if (newSchemeManager != null) {
            putInstance(ISchemeManager.class, newSchemeManager);
            return currentSchemeManager;
        }
        return null;
    }

    @Override
    public void setSchemeManager(ISchemeManager newSchemeManager) {
        checkBeforeModify();
        putInstance(ISchemeManager.class, newSchemeManager);
    }

    @Override
    public IResultSetReaderFactory changeResultSetReaderFactory(CreateNewInstance<IResultSetReaderFactory> resulSetReaderFactoryCreator) {
        checkBeforeModify();
        IResultSetReaderFactory currentResulSetReaderFactory = getInstance(IResultSetReaderFactory.class);
        IResultSetReaderFactory newResulSetReaderFactoryCreator = resulSetReaderFactoryCreator.createNew(currentResulSetReaderFactory);
        if (newResulSetReaderFactoryCreator != null) {
            putInstance(IResultSetReaderFactory.class, newResulSetReaderFactoryCreator);
            return currentResulSetReaderFactory;
        }
        return null;
    }

    @Override
    public void setResultSetReaderFactory(IResultSetReaderFactory resultSetReaderFactory) {
        checkBeforeModify();
        putInstance(IResultSetReaderFactory.class, resultSetReaderFactory);
    }

    @Override
    public IMethodInterceptor changeMethodInterceptor(CreateNewInstance<IMethodInterceptor> creator) {
        checkBeforeModify();
        IMethodInterceptor current = getInstance(IMethodInterceptor.class);
        IMethodInterceptor newInterceptor = creator.createNew(current);
        if (newInterceptor != null) {
            putInstance(IMethodInterceptor.class, newInterceptor);
            return current;
        }
        return null;
    }

    @Override
    public void setMethodInterceptor(IMethodInterceptor newInterceptor) {
        checkBeforeModify();
        putInstance(IMethodInterceptor.class, newInterceptor);
    }

    @Override
    public IJavaToSqlTypeResolver changeJavaToSqlTypeResolver(CreateNewInstance<IJavaToSqlTypeResolver> creator) {
        checkBeforeModify();
        IJavaToSqlTypeResolver current = getInstance(IJavaToSqlTypeResolver.class);
        IJavaToSqlTypeResolver newResolver = creator.createNew(current);
        if (newResolver != null) {
            putInstance(IJavaToSqlTypeResolver.class, newResolver);
            return current;
        }
        return null;
    }

    @Override
    public void setJavaToSqlTypeResolver(IJavaToSqlTypeResolver newResolver) {
        checkBeforeModify();
        putInstance(IJavaToSqlTypeResolver.class, newResolver);
    }

    @Override
    public void sealed() {
        if (!sealed) {
            ColumnValueConverterFactory columnValueFactory = getInstance(ColumnValueConverterFactory.class);
            columnValueFactory.sealedCustom();
            ResultSetValueReaderFactory resultSetValueReaderFactory = getInstance(ResultSetValueReaderFactory.class);
            resultSetValueReaderFactory.sealedCustom();
            ParamSetterFactory paramSetterfactory = getInstance(ParamSetterFactory.class);
            paramSetterfactory.sealedCustom();
            sealed = true;
        }
    }

    private void checkBeforeModify() {
        if (sealed) {
            throw new IllegalStateException("Конфигурация зафиксирована и не может быть изменена");
        }
    }
}