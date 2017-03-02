package com.reforms.orm.select.bobj;

import com.reforms.orm.OrmContext;
import com.reforms.orm.reflex.IInstanceBuilder;
import com.reforms.orm.reflex.IReflexor;
import com.reforms.orm.select.ColumnAlias;
import com.reforms.orm.select.SelectedColumn;
import com.reforms.orm.select.bobj.reader.IParamRsReader;
import com.reforms.orm.select.bobj.reader.ParamRsReaderFactory;

import java.sql.ResultSet;
import java.util.List;

public class ResultSetOrmReader {

    private List<SelectedColumn> columns;
    private IReflexor reflexor;
    private ParamRsReaderFactory paramRsReaderFactory;
    private IResultSetValueAdapter valueAdapter;
    private IInstanceBuilder ormInstanceBuilder;

    public ResultSetOrmReader(List<SelectedColumn> columns, IReflexor reflexor, OrmContext rCtx) {
        this.columns = columns;
        this.reflexor = reflexor;
        paramRsReaderFactory = rCtx.getParamRsReaderFactory();
        valueAdapter = rCtx.getResultSetValueAdapter();
        ormInstanceBuilder = reflexor.createInstanceBuilder();
    }

    public Object read(ResultSet rs) throws Exception {
        if (!rs.next()) {
            return null;
        }
        ormInstanceBuilder.prepare();
        for (SelectedColumn column : columns) {
            ColumnAlias cAlias = column.getColumnAlias();
            String metaFieldName = column.getFieldName();
            Class<?> clazz = reflexor.getType(metaFieldName);
            Object paramKey = clazz;
            if (cAlias.hasType()) {
                paramKey = cAlias.getAliasPrefix();
            } else if (clazz != null && (clazz.isEnum() || (clazz.isAnonymousClass() && clazz.getSuperclass().isEnum()))) {
                paramKey = Enum.class;
            }
            IParamRsReader<?> paramReader = paramRsReaderFactory.getParamRsReader(paramKey);
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
