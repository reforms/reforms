package com.reforms.orm.dao;

import com.reforms.orm.dao.filter.column.ISelectedColumnFilter;
import com.reforms.orm.dao.filter.param.IFilterValues;

/**
 * Контекст данных для совершения определенной операции
 * @author evgenie
 */
class DaoContext {

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

    Object getConnectionHolder() {
        return connectionHolder;
    }

    void setConnectionHolder(Object connectionHolder) {
        this.connectionHolder = connectionHolder;
    }

    String getQuery() {
        return query;
    }

    void setQuery(String query) {
        this.query = query;
    }

    Class<?> getOrmType() {
        return ormType;
    }

    void setOrmType(Class<?> ormType) {
        this.ormType = ormType;
    }

    ISelectedColumnFilter getSelectedColumnFilter() {
        return selectedColumnFilter;
    }

    void setSelectedColumnFilter(ISelectedColumnFilter selectedColumnFilter) {
        this.selectedColumnFilter = selectedColumnFilter;
    }

    IFilterValues getFilterValues() {
        return filterValues;
    }

    void setFilterValues(IFilterValues filterValues) {
        this.filterValues = filterValues;
    }

}
