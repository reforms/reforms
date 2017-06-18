package com.reforms.orm.dao;

import java.util.Iterator;

import com.reforms.orm.dao.bobj.update.IUpdateValues;

/**
 * Контекст данных для совершения определенной операции обновления в 'batch' режиме
 * @author evgenie
 */
class DaoBatchUpdateContext {

    /** Объект, который содержит доступ к БД */
    private Object connectionHolder;

    /** Запрос к БД */
    private String query;

    /** Размер пакета для отправки на сервер БД*/
    private int batchSize;

    private Iterator<IUpdateValues> upateValues;

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

    void setUpateValues(Iterator<IUpdateValues> upateValues) {
        this.upateValues = upateValues;
    }

    Iterator<IUpdateValues> getUpateValues() {
        return upateValues;
    }
}