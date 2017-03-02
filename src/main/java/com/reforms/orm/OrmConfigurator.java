package com.reforms.orm;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.reforms.ann.ThreadSafe;
import com.reforms.orm.filter.param.ParamSetterFactory;
import com.reforms.orm.reflex.LocalCache;
import com.reforms.orm.scheme.SchemeManager;
import com.reforms.orm.select.bobj.ColumnToFieldNameConverter;
import com.reforms.orm.select.bobj.ResultSetValueAdapter;
import com.reforms.orm.select.bobj.reader.ParamRsReaderFactory;
import com.reforms.orm.select.report.ColumnToRecordNameConverter;
import com.reforms.orm.select.report.converter.ColumnValueConverterFactory;
import com.reforms.sql.db.DbType;

public class OrmConfigurator {

    private static OrmConfigurator CONFIGURATOR = null;

    static {
        OrmConfigurator.defaultConfiguration();
    }

    public static void defaultConfiguration() {
        OrmConfigurator ormConf = new OrmConfigurator();
        OrmConfigurator.setInstance(ormConf);
        OrmContext ormContext = new OrmContext();
        ormConf.settings.put(OrmContext.class.getName(), ormContext);
        ColumnValueConverterFactory columnConverterFactory = new ColumnValueConverterFactory().configure(new ConverterConfig()).sealed();
        ormContext.setColumnValueConverterFactory(columnConverterFactory);
        ormContext.setColumnToRecordNameConverter(new ColumnToRecordNameConverter());
        ormContext.setConnectionHolder(new ReflexConnectionHolder());
        ParamSetterFactory paramSetterFactory = new ParamSetterFactory().configure().sealed();
        ormContext.setParamSetterFactory(paramSetterFactory);
        ParamRsReaderFactory paramRsReaderFactory = new ParamRsReaderFactory().configure().sealed();
        ormContext.setParamRsReaderFactory(paramRsReaderFactory);
        ormContext.setResultSetValueAdapter(new ResultSetValueAdapter());
        ormContext.setColumnToFieldNameConverter(new ColumnToFieldNameConverter());
        ormContext.setReflexorCache(new LocalCache());
        SchemeManager schemeManager = new SchemeManager();
        schemeManager.setDefaultDbType(DbType.MIX);
        ormContext.setSchemeManager(schemeManager);
    }

    public static OrmConfigurator getInstance() {
        return CONFIGURATOR;
    }

    public static void setInstance(OrmConfigurator configurator) {
        CONFIGURATOR = configurator;
    }


    public static <T> T get(Class<T> clazz) {
        Object value = CONFIGURATOR.settings.get(clazz.getName());
        if (value == null) {
            if (! clazz.isAnnotationPresent(ThreadSafe.class)) {
                throw new IllegalStateException("Необходимо указать аннотацию com.reforms.ann.ThreadSafe для класса '" + clazz + "'");
            }
            synchronized (CONFIGURATOR.settings) {
                try {
                    value = clazz.newInstance();
                } catch (ReflectiveOperationException roe) {
                    throw new IllegalStateException("Невозможно создать экземпляр класса '" + clazz + "'", roe);
                }
                CONFIGURATOR.settings.put(clazz.getName(), value);
            }
        }
        return  (T) value;
    }

    private Map<String, Object> settings = new ConcurrentHashMap<>();

}
