package com.reforms.orm.dao;

import com.reforms.orm.dao.filter.IFilterValues;

/**
 * Контекст данных для совершения определенной операции обновления
 * @author evgenie
 */
class DaoDeleteContext {

    /** Объект, который содержит доступ к БД */
    private Object connectionHolder;

    /** Запрос к БД */
    private String query;

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

    IFilterValues getFilterValues() {
        return filterValues;
    }

    void setFilterValues(IFilterValues filterValues) {
        this.filterValues = filterValues;
    }
}