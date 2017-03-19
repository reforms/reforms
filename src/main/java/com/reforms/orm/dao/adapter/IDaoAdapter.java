package com.reforms.orm.dao.adapter;

import java.util.List;

import com.reforms.orm.dao.OrmHandler;
import com.reforms.orm.dao.OrmIterator;

public interface IDaoAdapter {

    <OrmType> OrmType load(Class<OrmType> ormClass) throws Exception;

    <OrmType> List<OrmType> loads(Class<OrmType> ormClass) throws Exception;

    <OrmType> OrmIterator<OrmType> iterate(Class<OrmType> ormClass) throws Exception;

    <OrmType> void handle(Class<OrmType> ormClass, OrmHandler<OrmType> handler) throws Exception;
}
