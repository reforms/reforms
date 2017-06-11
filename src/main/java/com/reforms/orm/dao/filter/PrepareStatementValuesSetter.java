package com.reforms.orm.dao.filter;

import com.reforms.orm.dao.column.ColumnAliasType;
import com.reforms.orm.dao.filter.param.ParamSetter;
import com.reforms.orm.dao.filter.param.ParamSetterFactory;

import java.lang.reflect.Array;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Устанавливает значения с заданного индекса в PrepareStatement
 * @author evgenie
 */
public class PrepareStatementValuesSetter {

    private int index;

    private List<ParamSetter> paramSetters = new ArrayList<>();
    private List<Object> filterValues = new ArrayList<>();
    private ParamSetterFactory paramSetterFactory;

    public PrepareStatementValuesSetter(ParamSetterFactory paramSetterFactory) {
        this(1, paramSetterFactory);
    }

    public PrepareStatementValuesSetter(int index, ParamSetterFactory paramSetterFactory) {
        this.index = index;
        this.paramSetterFactory = paramSetterFactory;
    }

    public int addFilterValue(Object value) {
        ColumnAliasType filterType = getFilterType(value);
        return addFilterValue(filterType.getMarker(), value);
    }

    private ColumnAliasType getFilterType(Object filterValue) {
        String prefix = paramSetterFactory.findParamSetterMarker(filterValue);
        if (prefix != null) {
            return ColumnAliasType.getType(prefix.charAt(0));
        }
        if (filterValue instanceof Iterable) {
            Iterable<?> values = (Iterable<?>) filterValue;
            Iterator<?> iterator = values.iterator();
            if (!iterator.hasNext()) {
                throw new IllegalStateException("Определение типа фильтра для итерируемого объекта не допускается");
            }
            return getFilterType(iterator.next());
        }
        if (filterValue != null && filterValue.getClass().isArray()) {
            if (Array.getLength(filterValue) == 0) {
                throw new IllegalStateException("Определение типа фильтра для пустого массива не допускается");
            }
            return getFilterType(Array.get(filterValue, 0));
        }
        throw new IllegalStateException("Для класса '" + (filterValue == null ? "null" : filterValue.getClass()) +
                "' не найдет тип для установки фильтра");
    }

    public int addFilterValue(String filterPrefix, Object filterValue) {
        int newParamCount = 0;
        ParamSetter paramSetter = paramSetterFactory.getParamSetter(filterPrefix);
        if (paramSetter == null) {
            throw new IllegalStateException("Не определен paramSetter данных для префикса '" + filterPrefix + "'");
        }
        if (filterValue instanceof Iterable) {
            Iterable<?> values = (Iterable<?>) filterValue;
            Iterator<?> iteratorByValues = values.iterator();
            if (!iteratorByValues.hasNext()) {
                throw new IllegalStateException("Отсутствие значений в итерируемом объекте не допускается");
            }
            while (iteratorByValues.hasNext()) {
                Object filterValueFromCollection = iteratorByValues.next();
                paramSetters.add(paramSetter);
                filterValues.add(filterValueFromCollection);
                newParamCount++;
            }
        } else if (filterValue != null && filterValue.getClass().isArray()) {
            if (Array.getLength(filterValue) == 0) {
                throw new IllegalStateException("Пустой массив не допускается");
            }
            int length = Array.getLength(filterValue);
            for (int dataIndex = 0; dataIndex < length; dataIndex++) {
                Object filterValueFromArray = Array.get(filterValue, dataIndex);
                paramSetters.add(paramSetter);
                filterValues.add(filterValueFromArray);
                newParamCount++;
            }
        } else {
            paramSetters.add(paramSetter);
            filterValues.add(filterValue);
            newParamCount++;
        }
        return newParamCount;
    }

    public int setParamsTo(PreparedStatement ps) throws SQLException {
        for (int dataIndex = 0; dataIndex < filterValues.size(); dataIndex++) {
            Object filterValue = filterValues.get(dataIndex);
            ParamSetter paramSetter = paramSetters.get(dataIndex);
            paramSetter.setValue(filterValue, index, ps);
            index++;
        }
        return index;
    }
}
