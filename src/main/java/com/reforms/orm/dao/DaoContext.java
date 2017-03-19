package com.reforms.orm.dao;

import com.reforms.orm.filter.IFilterValues;
import com.reforms.orm.selectable.ISelectedColumnFilter;

/**
 * Контекст данных для совершения определенной операции
 * @author evgenie
 */
public class DaoContext {

    /** Объект, который содержит доступ к БД */
    private Object connectionHolder;

    /** Запрос к БД */
    private String query;

    /** Выбираемый тип */
    private Class<?> ormType;

    /** Фильтр выбираемых колонок */
    private ISelectedColumnFilter selectedColumnFilter;

    /** Фильтр данных */
    private IFilterValues filterValues;

    public Object getConnectionHolder() {
        return connectionHolder;
    }

    public void setConnectionHolder(Object connectionHolder) {
        this.connectionHolder = connectionHolder;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Class<?> getOrmType() {
        return ormType;
    }

    public void setOrmType(Class<?> ormType) {
        this.ormType = ormType;
    }

    public ISelectedColumnFilter getSelectedColumnFilter() {
        return selectedColumnFilter;
    }

    public void setSelectedColumnFilter(ISelectedColumnFilter selectedColumnFilter) {
        this.selectedColumnFilter = selectedColumnFilter;
    }

    public IFilterValues getFilterValues() {
        return filterValues;
    }

    public void setFilterValues(IFilterValues filterValues) {
        this.filterValues = filterValues;
    }

}
