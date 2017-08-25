package com.reforms.orm.dao;

import com.reforms.orm.dao.bobj.update.IUpdateValues;
import com.reforms.orm.dao.filter.IFilterValues;

/**
 * Контекст данных для совершения определенной операции обновления
 * @author evgenie
 */
class DaoUpdateContext extends DaoContext {

    private IUpdateValues upateValues;

    /** Фильтр данных */
    private IFilterValues filterValues;

    DaoUpdateContext(Object connectionHolder, String query) {
        super(connectionHolder, query);
    }

    IUpdateValues getUpateValues() {
        return upateValues;
    }

    void setUpateValues(IUpdateValues upateValues) {
        this.upateValues = upateValues;
    }

    IFilterValues getFilterValues() {
        return filterValues;
    }

    void setFilterValues(IFilterValues filterValues) {
        this.filterValues = filterValues;
    }
}