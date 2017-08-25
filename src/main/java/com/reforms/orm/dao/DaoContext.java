package com.reforms.orm.dao;

/**
 * Общий класс для контекстов
 * @author palihov
 */
class DaoContext {

    /** Объект, который содержит доступ к БД */
    private final Object connectionHolder;

    /** Запрос к БД */
    private final String query;

    DaoContext(Object connectionHolder, String query) {
        this.connectionHolder = connectionHolder;
        this.query = query;
    }

    Object getConnectionHolder() {
        return connectionHolder;
    }

    String getQuery() {
        return query;
    }

}
