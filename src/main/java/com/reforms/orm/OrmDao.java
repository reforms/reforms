package com.reforms.orm;

import static com.reforms.orm.filter.FilterMap.EMPTY_FILTER_MAP;
import static com.reforms.orm.selectable.AllSelectedColumnFilter.ALL_COLUMNS_FILTER;

import java.util.List;

import com.reforms.orm.dao.OrmDaoAdapter;
import com.reforms.orm.dao.OrmHandler;
import com.reforms.orm.dao.OrmIterator;
import com.reforms.orm.filter.FilterObject;
import com.reforms.orm.filter.FilterSequence;
import com.reforms.orm.filter.IFilterValues;
import com.reforms.orm.selectable.ISelectedColumnFilter;

public class OrmDao {

    private Object connectionHolder;

    public OrmDao(Object connectionHolder) {
        this.connectionHolder = connectionHolder;
    }

    public <OrmType> OrmType loadOrm(Class<OrmType> ormClass, String sqlQuery, Object filterBobj) throws Exception {
        return loadOrm(ormClass, sqlQuery, ALL_COLUMNS_FILTER, new FilterObject(filterBobj));
    }

    public <OrmType> OrmType loadOrm(Class<OrmType> ormClass, String sqlQuery, ISelectedColumnFilter solumnFilter, Object filterBobj)
            throws Exception {
        return loadOrm(ormClass, sqlQuery, solumnFilter, new FilterObject(filterBobj));
    }

    public <OrmType> OrmType loadOrm(Class<OrmType> ormClass, String sqlQuery, IFilterValues filters) throws Exception {
        return loadOrm(ormClass, sqlQuery, ALL_COLUMNS_FILTER, filters);
    }

    public <OrmType> OrmType loadSimpleOrm(Class<OrmType> ormClass, String sqlQuery, Object... filters) throws Exception {
        return loadOrm(ormClass, sqlQuery, ALL_COLUMNS_FILTER, new FilterSequence(filters));
    }

    public <OrmType> OrmType loadSimpleOrm(Class<OrmType> ormClass, String sqlQuery, ISelectedColumnFilter solumnFilter, Object... filters)
            throws Exception {
        return loadOrm(ormClass, sqlQuery, solumnFilter, new FilterSequence(filters));
    }

    public <OrmType> OrmType loadOrm(Class<OrmType> ormClass, String sqlQuery, ISelectedColumnFilter solumnFilter, IFilterValues filters)
            throws Exception {
        OrmDaoAdapter daoAdapter = new OrmDaoAdapter(connectionHolder, sqlQuery);
        daoAdapter.setSelectedColumnFilter(solumnFilter);
        daoAdapter.setFilterValue(filters);
        return daoAdapter.load(ormClass);
    }

    public <OrmType> List<OrmType> loadOrms(Class<OrmType> ormClass, String sqlQuery) throws Exception {
        return loadOrms(ormClass, sqlQuery, ALL_COLUMNS_FILTER, EMPTY_FILTER_MAP);
    }

    public <OrmType> List<OrmType> loadOrms(Class<OrmType> ormClass, String sqlQuery, ISelectedColumnFilter solumnFilter) throws Exception {
        return loadOrms(ormClass, sqlQuery, solumnFilter, EMPTY_FILTER_MAP);
    }

    public <OrmType> List<OrmType> loadOrms(Class<OrmType> ormClass, String sqlQuery, Object filterBobj) throws Exception {
        return loadOrms(ormClass, sqlQuery, ALL_COLUMNS_FILTER, new FilterObject(filterBobj));
    }

    public <OrmType> List<OrmType> loadOrms(Class<OrmType> ormClass, String sqlQuery, IFilterValues filter) throws Exception {
        return loadOrms(ormClass, sqlQuery, ALL_COLUMNS_FILTER, filter);
    }

    public <OrmType> List<OrmType> loadOrms(Class<OrmType> ormClass, String sqlQuery, ISelectedColumnFilter solumnFilter, Object filterBobj)
            throws Exception {
        return loadOrms(ormClass, sqlQuery, solumnFilter, new FilterObject(filterBobj));
    }

    public <OrmType> List<OrmType> loadSimpleOrms(Class<OrmType> ormClass, String sqlQuery, Object... filters) throws Exception {
        return loadOrms(ormClass, sqlQuery, ALL_COLUMNS_FILTER, new FilterSequence(filters));
    }

    public <OrmType> List<OrmType> loadSimpleOrms(Class<OrmType> ormClass, String sqlQuery, ISelectedColumnFilter solumnFilter,
            Object... filters) throws Exception {
        return loadOrms(ormClass, sqlQuery, solumnFilter, new FilterSequence(filters));
    }

    public <OrmType> List<OrmType> loadOrms(Class<OrmType> ormClass, String sqlQuery, ISelectedColumnFilter solumnFilter,
            IFilterValues filters) throws Exception {
        OrmDaoAdapter daoAdapter = new OrmDaoAdapter(connectionHolder, sqlQuery);
        daoAdapter.setSelectedColumnFilter(solumnFilter);
        daoAdapter.setFilterValue(filters);
        return daoAdapter.loads(ormClass);
    }

    public <OrmType> void handleOrms(Class<OrmType> ormClass, String sqlQuery, OrmHandler<OrmType> handler, Object filterBobj)
            throws Exception {
        handleOrms(ormClass, sqlQuery, handler, ALL_COLUMNS_FILTER, new FilterObject(filterBobj));
    }

    public <OrmType> void handleOrms(Class<OrmType> ormClass, String sqlQuery, OrmHandler<OrmType> handler, IFilterValues filter)
            throws Exception {
        handleOrms(ormClass, sqlQuery, handler, ALL_COLUMNS_FILTER, filter);
    }

    public <OrmType> void handleOrms(Class<OrmType> ormClass, String sqlQuery, OrmHandler<OrmType> handler,
            ISelectedColumnFilter solumnFilter, Object filterBobj)
            throws Exception {
        handleOrms(ormClass, sqlQuery, handler, solumnFilter, new FilterObject(filterBobj));
    }

    public <OrmType> void handleOrms(Class<OrmType> ormClass, String sqlQuery, OrmHandler<OrmType> handler) throws Exception {
        handleOrms(ormClass, sqlQuery, handler, ALL_COLUMNS_FILTER, EMPTY_FILTER_MAP);
    }

    public <OrmType> void handleOrms(Class<OrmType> ormClass, String sqlQuery, OrmHandler<OrmType> handler,
            ISelectedColumnFilter solumnFilter) throws Exception {
        handleOrms(ormClass, sqlQuery, handler, solumnFilter, EMPTY_FILTER_MAP);
    }

    public <OrmType> void handleSimpleOrms(Class<OrmType> ormClass, String sqlQuery, OrmHandler<OrmType> handler, Object... filters)
            throws Exception {
        handleOrms(ormClass, sqlQuery, handler, ALL_COLUMNS_FILTER, new FilterSequence(filters));
    }

    public <OrmType> void handleSimpleOrms(Class<OrmType> ormClass, String sqlQuery, OrmHandler<OrmType> handler,
            ISelectedColumnFilter solumnFilter, Object... filters)
            throws Exception {
        handleOrms(ormClass, sqlQuery, handler, solumnFilter, new FilterSequence(filters));
    }

    public <OrmType> void handleOrms(Class<OrmType> ormClass, String sqlQuery, OrmHandler<OrmType> handler,
            ISelectedColumnFilter solumnFilter, IFilterValues filters)
            throws Exception {
        OrmDaoAdapter daoAdapter = new OrmDaoAdapter(connectionHolder, sqlQuery);
        daoAdapter.setSelectedColumnFilter(solumnFilter);
        daoAdapter.setFilterValue(filters);
        daoAdapter.handle(ormClass, handler);
    }

    public <OrmType> OrmIterator<OrmType> loadOrmIterator(Class<OrmType> ormClass, String sqlQuery, Object filterBobj) throws Exception {
        return loadOrmIterator(ormClass, sqlQuery, ALL_COLUMNS_FILTER, new FilterObject(filterBobj));
    }

    public <OrmType> OrmIterator<OrmType> loadOrmIterator(Class<OrmType> ormClass, String sqlQuery, IFilterValues filter) throws Exception {
        return loadOrmIterator(ormClass, sqlQuery, ALL_COLUMNS_FILTER, filter);
    }

    public <OrmType> OrmIterator<OrmType> loadOrmIterator(Class<OrmType> ormClass, String sqlQuery, ISelectedColumnFilter solumnFilter,
            Object filterBobj) throws Exception {
        return loadOrmIterator(ormClass, sqlQuery, solumnFilter, new FilterObject(filterBobj));
    }

    public <OrmType> OrmIterator<OrmType> loadOrmIterator(Class<OrmType> ormClass, String sqlQuery) throws Exception {
        return loadOrmIterator(ormClass, sqlQuery, ALL_COLUMNS_FILTER, EMPTY_FILTER_MAP);
    }

    public <OrmType> OrmIterator<OrmType> loadOrmIterator(Class<OrmType> ormClass, String sqlQuery, ISelectedColumnFilter solumnFilter)
            throws Exception {
        return loadOrmIterator(ormClass, sqlQuery, solumnFilter, EMPTY_FILTER_MAP);
    }

    public <OrmType> OrmIterator<OrmType> loadSimpleOrmIterator(Class<OrmType> ormClass, String sqlQuery, Object... filters)
            throws Exception {
        return loadOrmIterator(ormClass, sqlQuery, ALL_COLUMNS_FILTER, new FilterSequence(filters));
    }

    public <OrmType> OrmIterator<OrmType> loadSimpleOrmIterator(Class<OrmType> ormClass, String sqlQuery,
            ISelectedColumnFilter solumnFilter, Object... filters)
            throws Exception {
        return loadOrmIterator(ormClass, sqlQuery, solumnFilter, new FilterSequence(filters));
    }

    public <OrmType> OrmIterator<OrmType> loadOrmIterator(Class<OrmType> ormClass, String sqlQuery, ISelectedColumnFilter solumnFilter,
            IFilterValues filters) throws Exception {
        OrmDaoAdapter daoAdapter = new OrmDaoAdapter(connectionHolder, sqlQuery);
        daoAdapter.setSelectedColumnFilter(solumnFilter);
        daoAdapter.setFilterValue(filters);
        return daoAdapter.iterate(ormClass);
    }

}
