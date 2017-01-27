package com.reforms.orm;

import com.reforms.orm.filter.param.ParamSetterFactory;
import com.reforms.orm.reflex.ReflexorCache;
import com.reforms.orm.scheme.SchemeManager;
import com.reforms.orm.select.bobj.ColumnToFieldNameConverter;
import com.reforms.orm.select.bobj.ResultSetValueAdapter;
import com.reforms.orm.select.bobj.reader.ParamRsReaderFactory;
import com.reforms.orm.select.report.ColumnToRecordNameConverter;
import com.reforms.orm.select.report.converter.ColumnValueConverterFactory;
import com.reforms.sql.db.DbType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OrmConfigurator {

    private static OrmConfigurator CONFIGURATOR = null;

    static {
        OrmConfigurator.defaultConfiguration();
    }

    public static void defaultConfiguration() {
        OrmConfigurator ormConf = new OrmConfigurator();
        OrmContext ormContext = new OrmContext();
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
        ormContext.setReflexorCache(new ReflexorCache());
        SchemeManager schemeManager = new SchemeManager();
        schemeManager.setDefaultDbType(DbType.MIX);
        ormContext.setSchemeManager(schemeManager);
        ormConf.configureReportEngine(ormContext);
        OrmConfigurator.setInstance(ormConf);
    }

    public static OrmConfigurator getInstance() {
        return CONFIGURATOR;
    }

    public static void setInstance(OrmConfigurator configurator) {
        CONFIGURATOR = configurator;
    }

    public static <T> T get(String key) {
        return (T) CONFIGURATOR.settings.get(key);
    }

    public static <T> T get(Class<T> clazz) {
        return (T) CONFIGURATOR.settings.get(clazz.getName());
    }

    private Map<String, Object> settings = new ConcurrentHashMap<>();

    public void configureReportEngine(OrmContext reportContext) {
        settings.put(OrmContext.class.getName(), reportContext);
    }

}
