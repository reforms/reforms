package com.reforms.orm.dao;

import com.reforms.orm.dao.bobj.update.IInsertValues;

import java.util.Iterator;

/**
 * Контекст данных для совершения определенной операции встакви в 'batch' режиме
 * @author evgenie
 */
class DaoBatchInsertContext {

    /** Объект, который содержит доступ к БД */
    private Object connectionHolder;

    /** Запрос к БД */
    private String query;

    /** Размер пакета для отправки на сервер БД*/
    private int batchSize;

    private Iterator<IInsertValues> insertValues;

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

    void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    int getBatchSize() {
        return batchSize;
    }

    public void setInsertValues(Iterator<IInsertValues> insertValues) {
        this.insertValues = insertValues;
    }

    public Iterator<IInsertValues> getInsertValues() {
        return insertValues;
    }
}