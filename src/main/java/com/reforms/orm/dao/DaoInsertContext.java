package com.reforms.orm.dao;

import com.reforms.orm.dao.bobj.update.IInsertValues;
import com.reforms.orm.dao.filter.column.ISelectedColumnFilter;

/**
 * Контекст данных для совершения определенной операции обновления
 * @author evgenie
 */
class DaoInsertContext extends DaoContext {

    /** Данные для вставки */
    private IInsertValues insertValues;

    /** Фильтр выбираемых колонок */
    private ISelectedColumnFilter returningColumnFilter;

    /** Возращаемое значение в insert есть (поле автоинкремент) */
    private Class<?> keyClass;

    DaoInsertContext(Object connectionHolder, String query) {
        super(connectionHolder, query);
    }

    IInsertValues getInsertValues() {
        return insertValues;
    }

    void setInsertValues(IInsertValues insertValues) {
        this.insertValues = insertValues;
    }

    void setKeyClass(Class<?> keyClass) {
        this.keyClass = keyClass;
    }

    Class<?> getKeyClass() {
        return keyClass;
    }

    public void setReturningColumnFilter(ISelectedColumnFilter returningColumnFilter) {
        this.returningColumnFilter = returningColumnFilter;
    }

    public ISelectedColumnFilter getReturningColumnFilter() {
        return returningColumnFilter;
    }

}