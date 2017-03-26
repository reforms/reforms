package com.reforms.orm.dao;

import com.reforms.orm.dao.bobj.model.OrmHandler;
import com.reforms.orm.dao.bobj.model.OrmIterator;

import java.util.List;

/**
 * Контракт на совершение базовых операций к БД
 * @author evgenie
 */
interface IOrmDao {

    <OrmType> OrmType load(DaoSelectContext daoCtx) throws Exception;

    <OrmType> List<OrmType> loads(DaoSelectContext daoCtx) throws Exception;

    <OrmType> OrmIterator<OrmType> iterate(DaoSelectContext daoCtx) throws Exception;

    void handle(DaoSelectContext daoCtx, OrmHandler<Object> handler) throws Exception;

    int update(DaoUpdateContext daoCtx) throws Exception;

    int delete(DaoDeleteContext daoCtx) throws Exception;
}
