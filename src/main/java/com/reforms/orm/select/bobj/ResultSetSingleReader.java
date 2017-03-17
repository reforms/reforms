package com.reforms.orm.select.bobj;

import com.reforms.orm.select.ColumnAlias;
import com.reforms.orm.select.IResultSetReader;
import com.reforms.orm.select.SelectedColumn;
import com.reforms.orm.select.bobj.reader.IParamRsReader;
import com.reforms.orm.select.bobj.reader.ParamRsReaderFactory;

import java.sql.ResultSet;
import java.util.List;

import static com.reforms.orm.OrmConfigurator.getInstance;
import static com.reforms.orm.reflex.ClassUtils.isEnumClass;

/**
 * Вычитываем простые объекты и примитивы не требующие логики создания: int, String, BigDecimal и т.д.
 * @author evgenie
 */
public class ResultSetSingleReader implements IResultSetReader {

    private Class<?> itemClass;

    private SelectedColumn column;
    private ParamRsReaderFactory paramRsReaderFactory;
    private IResultSetValueAdapter valueAdapter;

    public ResultSetSingleReader(Class<?> itemClass, List<SelectedColumn> columns) {
        this.itemClass = itemClass;
        column = findSelectedColumn(columns);
        paramRsReaderFactory = getInstance(ParamRsReaderFactory.class);
        valueAdapter = getInstance(IResultSetValueAdapter.class);
    }

    @Override
    public boolean canRead(ResultSet rs) throws Exception {
        return rs.next();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object read(ResultSet rs) throws Exception {
        ColumnAlias cAlias = column.getColumnAlias();
        Class<?> clazz = itemClass;
        Object paramKey = clazz;
        if (cAlias.hasType()) {
            paramKey = cAlias.getAliasPrefix();
        } else if (isEnumClass(clazz)) {
            paramKey = Enum.class;
        }
        IParamRsReader<?> paramReader = paramRsReaderFactory.getParamRsReader(paramKey);
        if (paramReader == null) {
            throw new IllegalStateException("Не найден IParamRsReader для чтения из ResultSet значения для '" + paramKey + "'");
        }
        Object paramValue = paramReader.readValue(column, rs, clazz);
        Object adaptedValue = valueAdapter.adapt(column, paramValue, clazz);
        return adaptedValue;
    }

    /**
     * TODO продумать вопрос, что делать, если колонок больше 1??? Сейчас просто возвращаем первую
     * @param columns
     * @return
     */
    private SelectedColumn findSelectedColumn(List<SelectedColumn> columns) {
        return columns.get(0);
    }
}