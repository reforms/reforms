package com.reforms.orm;

import com.reforms.ann.TargetApi;
import com.reforms.ann.ThreadSafe;
import com.reforms.orm.dao.*;
import com.reforms.orm.dao.bobj.ColumnToFieldNameConverter;
import com.reforms.orm.dao.bobj.IColumnToFieldNameConverter;
import com.reforms.orm.dao.bobj.IResultSetValueAdapter;
import com.reforms.orm.dao.bobj.ResultSetValueAdapter;
import com.reforms.orm.dao.bobj.reader.ResultSetValueReaderFactory;
import com.reforms.orm.dao.filter.param.ParamSetterFactory;
import com.reforms.orm.dao.proxy.DefaultMethodInterceptor;
import com.reforms.orm.dao.proxy.GenericTypeScanner;
import com.reforms.orm.dao.proxy.IMethodInterceptor;
import com.reforms.orm.dao.report.ColumnToRecordNameConverter;
import com.reforms.orm.dao.report.IColumnToRecordNameConverter;
import com.reforms.orm.dao.report.converter.ColumnValueConverterFactory;
import com.reforms.orm.reflex.IGenericTypeResolver;
import com.reforms.orm.reflex.LocalCache;
import com.reforms.orm.scheme.ISchemeManager;
import com.reforms.orm.scheme.SchemeManager;
import com.reforms.sql.db.DbType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@TargetApi
public class OrmConfigurator {

    private static final Map<String, Object> SETTINGS = new ConcurrentHashMap<>();

    static {
        OrmConfigurator.defaultConfiguration();
    }

    private OrmConfigurator() {
    }

    public static void defaultConfiguration() {
        putInstance(IOrmContext.class, new OrmContext());
        ColumnValueConverterFactory columnConverterFactory = new ColumnValueConverterFactory().configure(new ConverterConfig()).sealed();
        putInstance(ColumnValueConverterFactory.class, columnConverterFactory);
        putInstance(IColumnToRecordNameConverter.class, new ColumnToRecordNameConverter());
        putInstance(IConnectionHolder.class, new ReflexConnectionHolder());
        putInstance(ParamSetterFactory.class, new ParamSetterFactory().configure().sealed());
        putInstance(ResultSetValueReaderFactory.class, new ResultSetValueReaderFactory().configure().sealed());
        putInstance(IResultSetValueAdapter.class, new ResultSetValueAdapter());
        putInstance(IColumnToFieldNameConverter.class, new ColumnToFieldNameConverter());
        putInstance(IParamNameConverter.class, new ParamNameConverter());
        putInstance(IResultSetReaderFactory.class, new ResultSetReaderFactory());
        putInstance(LocalCache.class, new LocalCache());
        SchemeManager schemeManager = new SchemeManager();
        schemeManager.setDefaultDbType(DbType.DBT_MIX);
        putInstance(ISchemeManager.class, schemeManager);
        putInstance(IMethodInterceptor.class, new DefaultMethodInterceptor());
        putInstance(IJavaToSqlTypeResolver.class, new JavaToSqlTypeResolver());
        putInstance(IGenericTypeResolver.class, new GenericTypeScanner(true, true));
        putInstance(IQuerySniffer.class, new QuerySniffer());
    }

    static void putInstance(Class<?> clazz, Object instance) {
        SETTINGS.put(clazz.getName(), instance);
    }

    /**
     * TODO не правильная синхронизация
     * @param clazz класс
     * @return экземпляр класса
     */
    @SuppressWarnings("unchecked")
    public static <T> T getInstance(Class<T> clazz) {
        T value = (T) SETTINGS.get(clazz.getName());
        if (value == null) {
            if (! clazz.isAnnotationPresent(ThreadSafe.class)) {
                throw new IllegalStateException("No com.reforms.ann.ThreadSafe annotation present for class '" + clazz + "'");
            }
            try {
                value = clazz.newInstance();
            } catch (ReflectiveOperationException roe) {
                throw new IllegalStateException("Unable to create instance of '" + clazz + "'", roe);
            }
            putInstance(clazz, value);
        }
        return value;
    }

}
