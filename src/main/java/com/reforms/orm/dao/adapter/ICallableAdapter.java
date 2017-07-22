package com.reforms.orm.dao.adapter;

/**
 * Контракт на формирование типа хранимой процедуры
 * @author evgenie
 */
public interface ICallableAdapter<Adapter> {

    Adapter registryOutParam(Integer returnSqlType);
}