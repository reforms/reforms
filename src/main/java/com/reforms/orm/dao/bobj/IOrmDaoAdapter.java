package com.reforms.orm.dao.bobj;

import java.util.List;
import java.util.Set;

import com.reforms.orm.dao.adapter.*;
import com.reforms.orm.dao.bobj.model.OrmHandler;
import com.reforms.orm.dao.bobj.model.OrmIterator;

public interface IOrmDaoAdapter extends ISelectedColumnFilterAdapter<IOrmDaoAdapter>, IFilterValuesAdapter<IOrmDaoAdapter>, IPageFilterAdapter<IOrmDaoAdapter>,
        IUpdateValuesAdapter<IOrmDaoAdapter>, IInsertValuesAdapter<IOrmDaoAdapter> {

    <OrmType> OrmType load(Class<OrmType> ormClass) throws Exception;

    <OrmType> List<OrmType> loads(Class<OrmType> ormClass) throws Exception;

    <OrmType> Set<OrmType> set(Class<OrmType> ormClass) throws Exception;

    <OrmType> OrmIterator<OrmType> iterate(Class<OrmType> ormClass) throws Exception;

    <OrmType> void handle(Class<OrmType> ormClass, OrmHandler<OrmType> handler) throws Exception;

    int update() throws Exception;

    int delete() throws Exception;

    void insert() throws Exception;
}
