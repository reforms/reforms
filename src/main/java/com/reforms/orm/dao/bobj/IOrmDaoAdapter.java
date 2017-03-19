package com.reforms.orm.dao.bobj;

import java.util.List;

import com.reforms.orm.dao.adapter.IFilterValuesAdapter;
import com.reforms.orm.dao.adapter.IPageFilterAdapter;
import com.reforms.orm.dao.adapter.ISelectedColumnFilterAdapter;
import com.reforms.orm.dao.bobj.model.OrmHandler;
import com.reforms.orm.dao.bobj.model.OrmIterator;

public interface IOrmDaoAdapter extends ISelectedColumnFilterAdapter<IOrmDaoAdapter>,
                                     IFilterValuesAdapter<IOrmDaoAdapter>,
                                     IPageFilterAdapter<IOrmDaoAdapter> {

    <OrmType> OrmType load(Class<OrmType> ormClass) throws Exception;

    <OrmType> List<OrmType> loads(Class<OrmType> ormClass) throws Exception;

    <OrmType> OrmIterator<OrmType> iterate(Class<OrmType> ormClass) throws Exception;

    <OrmType> void handle(Class<OrmType> ormClass, OrmHandler<OrmType> handler) throws Exception;
}
