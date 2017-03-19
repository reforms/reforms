package com.reforms.orm;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.reforms.ann.ThreadSafe;
import com.reforms.orm.filter.param.ParamSetterFactory;
import com.reforms.orm.reflex.LocalCache;
import com.reforms.orm.scheme.ISchemeManager;
import com.reforms.orm.scheme.SchemeManager;
import com.reforms.orm.select.IResultSetReaderFactory;
import com.reforms.orm.select.ResultSetReaderFactory;
import com.reforms.orm.select.bobj.ColumnToFieldNameConverter;
import com.reforms.orm.select.bobj.IColumnToFieldNameConverter;
import com.reforms.orm.select.bobj.IResultSetValueAdapter;
import com.reforms.orm.select.bobj.ResultSetValueAdapter;
import com.reforms.orm.select.bobj.reader.ResultSetValueReaderFactory;
import com.reforms.orm.select.report.ColumnToRecordNameConverter;
import com.reforms.orm.select.report.IColumnToRecordNameConverter;
import com.reforms.orm.select.report.converter.ColumnValueConverterFactory;
import com.reforms.sql.db.DbType;

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
        putInstance(IResultSetReaderFactory.class, new ResultSetReaderFactory());
        putInstance(LocalCache.class, new LocalCache());
        SchemeManager schemeManager = new SchemeManager();
        schemeManager.setDefaultDbType(DbType.MIX);
        putInstance(ISchemeManager.class, schemeManager);
    }

    static void putInstance(Class<?> clazz, Object instance) {
        SETTINGS.put(clazz.getName(), instance);
    }

    /**
     * TODO не правильная синхронизация
     * @param clazz
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T getInstance(Class<T> clazz) {
        T value = (T) SETTINGS.get(clazz.getName());
        if (value == null) {
            if (! clazz.isAnnotationPresent(ThreadSafe.class)) {
                throw new IllegalStateException("Необходимо указать аннотацию com.reforms.ann.ThreadSafe для класса '" + clazz + "'");
            }
            try {
                value = clazz.newInstance();
            } catch (ReflectiveOperationException roe) {
                throw new IllegalStateException("Невозможно создать экземпляр класса '" + clazz + "'", roe);
            }
            putInstance(clazz, value);
        }
        return value;
    }

}
