package com.reforms.orm.dao;

import com.reforms.orm.dao.filter.IFilterValues;
import com.reforms.orm.dao.filter.column.ISelectedColumnFilter;

/**
 * Контекст данных для совершения определенной операции
 * @author evgenie
 */
class DaoCallContext extends DaoContext {

    /** Выбираемый тип */
    private Class<?> ormType;

    /** Фильтр выбираемых колонок */
    private ISelectedColumnFilter selectedColumnFilter;

    /** Фильтр данных */
    private IFilterValues filterValues;

    private Integer returnSqlType;

    DaoCallContext(Object connectionHolder, String query) {
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

    void setReturnSqlType(Integer returnSqlType) {
        this.returnSqlType = returnSqlType;
    }

    Integer getReturnSqlType() {
        return returnSqlType;
    }
}
