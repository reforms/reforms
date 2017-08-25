package com.reforms.orm.dao;

import com.reforms.orm.dao.filter.IFilterValues;

/**
 * Контекст данных для совершения определенной операции обновления
 * @author evgenie
 */
class DaoDeleteContext extends DaoContext {

    /** Фильтр данных */
    private IFilterValues filterValues;

    DaoDeleteContext(Object connectionHolder, String query) {
        super(connectionHolder, query);
    }

    IFilterValues getFilterValues() {
        return filterValues;
    }

    void setFilterValues(IFilterValues filterValues) {
        this.filterValues = filterValues;
    }
}