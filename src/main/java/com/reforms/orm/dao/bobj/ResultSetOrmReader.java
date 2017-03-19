package com.reforms.orm.dao.bobj;

import com.reforms.orm.dao.IResultSetObjectReader;
import com.reforms.orm.dao.bobj.reader.IResultSetValueReader;
import com.reforms.orm.dao.bobj.reader.ResultSetValueReaderFactory;
import com.reforms.orm.dao.column.ColumnAlias;
import com.reforms.orm.dao.column.SelectedColumn;
import com.reforms.orm.reflex.IInstanceBuilder;
import com.reforms.orm.reflex.IReflexor;

import java.sql.ResultSet;
import java.util.List;

import static com.reforms.orm.OrmConfigurator.getInstance;
import static com.reforms.orm.reflex.ClassUtils.isEnumClass;
import static com.reforms.orm.reflex.Reflexor.createReflexor;

public class ResultSetOrmReader implements IResultSetObjectReader {

    private List<SelectedColumn> columns;
    private IReflexor reflexor;
    private ResultSetValueReaderFactory resultSetValueReaderFactory;
    private IResultSetValueAdapter valueAdapter;
    private IInstanceBuilder ormInstanceBuilder;

    public ResultSetOrmReader(Class<?> ormClass, List<SelectedColumn> columns) {
        this.columns = columns;
        reflexor = createReflexor(ormClass);
        resultSetValueReaderFactory = getInstance(ResultSetValueReaderFactory.class);
        valueAdapter = getInstance(IResultSetValueAdapter.class);
        ormInstanceBuilder = reflexor.createInstanceBuilder();
    }

    @Override
    public boolean canRead(ResultSet rs) throws Exception {
        return rs.next();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object read(ResultSet rs) throws Exception {
        ormInstanceBuilder.prepare();
        for (SelectedColumn column : columns) {
            ColumnAlias cAlias = column.getColumnAlias();
            String metaFieldName = column.getFieldName();
            Class<?> clazz = reflexor.getType(metaFieldName);
            Object paramKey = clazz;
            if (cAlias.hasType()) {
                paramKey = cAlias.getAliasPrefix();
            } else if (isEnumClass(clazz)) {
                paramKey = Enum.class;
            }
            IResultSetValueReader<?> paramReader = resultSetValueReaderFactory.getParamRsReader(paramKey);
            if (paramReader == null) {
                throw new IllegalStateException("Не найден IParamRsReader для чтения из ResultSet значения для '" + paramKey + "'");
            }
            Object paramValue = paramReader.readValue(column, rs, clazz);
            Object adaptedValue = valueAdapter.adapt(column, paramValue, clazz);
            ormInstanceBuilder.append(metaFieldName, adaptedValue);
        }
        return ormInstanceBuilder.complete();
    }

}