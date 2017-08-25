package com.reforms.orm.dao;

import com.reforms.orm.dao.filter.IFilterValues;
import com.reforms.orm.dao.filter.column.ISelectedColumnFilter;

/**
 * Контекст данных для совершения определенной операции
 * @author evgenie
 */
class DaoSelectContext extends DaoContext {

    /** Выбираемый тип */
    private Class<?> ormType;

    /** Фильтр выбираемых колонок */
    private ISelectedColumnFilter selectedColumnFilter;

    /** Фильтр данных */
    private IFilterValues filterValues;

    DaoSelectContext(Object connectionHolder, String query) {
        super(connectionHolder, query);
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
