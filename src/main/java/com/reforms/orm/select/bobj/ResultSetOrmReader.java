package com.reforms.orm.select.bobj;

import java.sql.ResultSet;
import java.util.List;

import com.reforms.orm.OrmContext;
import com.reforms.orm.reflex.IReflexor;
import com.reforms.orm.select.ColumnAlias;
import com.reforms.orm.select.SelectedColumn;
import com.reforms.orm.select.bobj.reader.IParamRsReader;
import com.reforms.orm.select.bobj.reader.ParamRsReaderFactory;

public class ResultSetOrmReader {

    private List<SelectedColumn> columns;
    private IReflexor reflexor;
    private ParamRsReaderFactory paramRsReaderFactory;
    private IResultSetValueAdapter valueAdapter;
    private IColumnToFieldNameConverter columnToFieldNameConverter;

    public ResultSetOrmReader(List<SelectedColumn> columns, IReflexor reflexor, OrmContext rCtx) {
        this.columns = columns;
        this.reflexor = reflexor;
        this.paramRsReaderFactory = rCtx.getParamRsReaderFactory();
        this.valueAdapter = rCtx.getResultSetValueAdapter();
        this.columnToFieldNameConverter = rCtx.getColumnToFieldNameConverter();
    }

    public Object read(ResultSet rs) throws Exception {
        if (!rs.next()) {
            return null;
        }
        Object ormInstance = reflexor.createInstance();
        for (SelectedColumn column : columns) {
            ColumnAlias cAlias = column.getColumnAlias();
            String metaFieldName = columnToFieldNameConverter.getFieldName(column);
            Class<?> clazz = reflexor.getType(metaFieldName);
            Object paramKey = cAlias.hasType() ? cAlias.getAliasPrefix() : clazz;
            IParamRsReader<?> paramReader = paramRsReaderFactory.getParamRsReader(paramKey);
            Object paramValue = paramReader.readValue(column.getIndex(), rs);
            Object adaptedValue = valueAdapter.adapt(column, paramValue, clazz);
            reflexor.setValue(ormInstance, metaFieldName, adaptedValue);
        }
        return ormInstance;
    }

}
