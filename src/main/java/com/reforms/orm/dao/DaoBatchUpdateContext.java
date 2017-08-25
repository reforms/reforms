package com.reforms.orm.dao;

import com.reforms.orm.dao.bobj.update.IUpdateValues;

import java.util.Iterator;

/**
 * Контекст данных для совершения определенной операции обновления в 'batch' режиме
 * @author evgenie
 */
class DaoBatchUpdateContext extends DaoContext {

    /** Размер пакета для отправки на сервер БД*/
    private int batchSize;

    private Iterator<IUpdateValues> upateValues;

    DaoBatchUpdateContext(Object connectionHolder, String query) {
        super(connectionHolder, query);
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