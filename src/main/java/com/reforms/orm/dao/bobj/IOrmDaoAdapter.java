package com.reforms.orm.dao.bobj;

import com.reforms.orm.dao.adapter.*;
import com.reforms.orm.dao.bobj.model.OrmHandler;
import com.reforms.orm.dao.bobj.model.OrmIterator;

import java.util.List;
import java.util.Set;

/**
 * Определяет контракт выполнения дао методов
 * @author evgenie
 */
public interface IOrmDaoAdapter extends ISelectedColumnFilterAdapter<IOrmDaoAdapter>,
                                        IFilterValuesAdapter<IOrmDaoAdapter>,
                                        IPageFilterAdapter<IOrmDaoAdapter>,
                                        IUpdateValuesAdapter<IOrmDaoAdapter>,
                                        IInsertValuesAdapter<IOrmDaoAdapter>,
                                        ICallableAdapter<IOrmDaoAdapter> {

    <OrmType> OrmType load(Class<OrmType> ormClass) throws Exception;

    <OrmType> List<OrmType> loads(Class<OrmType> ormClass) throws Exception;

    <OrmType> Set<OrmType> set(Class<OrmType> ormClass) throws Exception;

    <OrmType> OrmIterator<OrmType> iterate(Class<OrmType> ormClass) throws Exception;

    <OrmType> void handle(Class<OrmType> ormClass, OrmHandler<OrmType> handler) throws Exception;

    int update() throws Exception;

    int[][] updates(int batchSize) throws Exception;

    int delete() throws Exception;

    void insert() throws Exception;

    int[][] inserts(int batchSize) throws Exception;

    <OrmType> OrmType callAndLoad(Class<OrmType> ormClass) throws Exception;

    <OrmType> List<OrmType> callAndLoads(Class<OrmType> ormClass) throws Exception;

    <OrmType> OrmIterator<OrmType> callAndIterate(Class<OrmType> ormClass) throws Exception;

    <OrmType> void callAndHandle(Class<OrmType> ormClass, OrmHandler<OrmType> handler) throws Exception;

}
