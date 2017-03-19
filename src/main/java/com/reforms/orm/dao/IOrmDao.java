package com.reforms.orm.dao;

import java.util.List;

import com.reforms.orm.dao.bobj.model.OrmHandler;
import com.reforms.orm.dao.bobj.model.OrmIterator;

/**
 * Контракт на совершение базовых операций к БД
 * @author evgenie
 */
interface IOrmDao {

    <OrmType> OrmType load(DaoContext daoCtx) throws Exception;

    <OrmType> List<OrmType> loads(DaoContext daoCtx) throws Exception;

    <OrmType> OrmIterator<OrmType> iterate(DaoContext daoCtx) throws Exception;

    void handle(DaoContext daoCtx, OrmHandler<Object> handler) throws Exception;

}
