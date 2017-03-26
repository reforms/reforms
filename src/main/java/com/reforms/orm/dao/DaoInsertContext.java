package com.reforms.orm.dao;

import com.reforms.orm.dao.bobj.update.IInsertValues;

/**
 * Контекст данных для совершения определенной операции обновления
 * @author evgenie
 */
class DaoInsertContext {

    /** Объект, который содержит доступ к БД */
    private Object connectionHolder;

    /** Запрос к БД */
    private String query;

    /** Данные для вставки */
    private IInsertValues insertValues;

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

    public IInsertValues getInsertValues() {
        return insertValues;
    }

    public void setInsertValues(IInsertValues insertValues) {
        this.insertValues = insertValues;
    }

}