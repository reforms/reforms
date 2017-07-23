package com.reforms.orm.dao;

import com.reforms.sql.db.DbType;

/**
 * Контракт, который определяет возращаемый тип храномиой процедуры по объекту
 * @author evgenie
 */
public interface IJavaToSqlTypeResolver {

    /**
     * Преобразует java тип в sql тип
     * @param returnType sql тип
     * @return sql тип или NULL
     */
    Integer getReturnSqlType(Class<?> returnType);

    /**
     * Возвращает тип курсора
     * @return тип курсора
     */
    Integer getCursorType(DbType dbType);
}
