package com.reforms.orm.dao.bobj.model;

/**
 * Обработка сущностей по одному.
 * @author evgenie
 * @param <T> класс сущностей
 */
@FunctionalInterface
public interface OrmHandler<T> {

    public default void startHandle() {
    }

    public boolean handleOrm(T orm);

    public default void endHandle() {
    }

}