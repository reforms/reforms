package com.reforms.orm.select.bobj.model;

/**
 * Обработка сущностей по одному.
 * @author evgenie
 * @param <T> класс сущностей
 */
public interface OrmHandler<T> {

    public void startHandle();

    public boolean handleOrm(T reportRecord); 

    public void endHandle();

}
