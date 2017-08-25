package com.reforms.orm.dao;

import com.reforms.orm.dao.bobj.update.IInsertValues;

import java.util.Iterator;

/**
 * Контекст данных для совершения определенной операции встакви в 'batch' режиме
 * @author evgenie
 */
class DaoBatchInsertContext extends DaoContext {

    /** Размер пакета для отправки на сервер БД*/
    private int batchSize;

    private Iterator<IInsertValues> insertValues;

    DaoBatchInsertContext(Object connectionHolder, String query) {
        super(connectionHolder, query);
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