package com.reforms.orm;

import com.reforms.orm.dao.OrmDaoAdapter;
import com.reforms.orm.dao.bobj.IOrmDaoAdapter;
import com.reforms.orm.dao.bobj.model.OrmHandler;
import com.reforms.orm.dao.bobj.model.OrmIterator;
import com.reforms.orm.dao.bobj.update.IUpdateValues;
import com.reforms.orm.dao.bobj.update.UpdateMap;
import com.reforms.orm.dao.bobj.update.UpdateObject;
import com.reforms.orm.dao.bobj.update.UpdateSequence;
import com.reforms.orm.dao.filter.FilterMap;
import com.reforms.orm.dao.filter.FilterObject;
import com.reforms.orm.dao.filter.FilterSequence;
import com.reforms.orm.dao.filter.IFilterValues;
import com.reforms.orm.dao.filter.column.ISelectedColumnFilter;

import java.util.List;
import java.util.Map;

import static com.reforms.orm.dao.filter.FilterMap.EMPTY_FILTER_MAP;
import static com.reforms.orm.dao.filter.column.AllSelectedColumnFilter.ALL_COLUMNS_FILTER;

/**
 * Фасадные методы для доступа к БД
 * @author evgenie
 */
public class OrmDao {

    private Object connectionHolder;

    public OrmDao(Object connectionHolder) {
        this.connectionHolder = connectionHolder;
    }

    public <OrmType> OrmType selectOrm(Class<OrmType> ormClass, String sqlQuery, Object filterBobj) throws Exception {
        return selectOrm(ormClass, sqlQuery, ALL_COLUMNS_FILTER, new FilterObject(filterBobj));
    }

    public <OrmType> OrmType selectOrm(Class<OrmType> ormClass, String sqlQuery, Map<String, Object> filterMap) throws Exception {
        return selectOrm(ormClass, sqlQuery, ALL_COLUMNS_FILTER, new FilterMap(filterMap));
    }

    public <OrmType> OrmType selectOrm(Class<OrmType> ormClass, String sqlQuery, ISelectedColumnFilter solumnFilter, Object filterBobj)
            throws Exception {
        return selectOrm(ormClass, sqlQuery, solumnFilter, new FilterObject(filterBobj));
    }

    public <OrmType> OrmType selectOrm(Class<OrmType> ormClass, String sqlQuery, ISelectedColumnFilter solumnFilter, Map<String, Object> filterMap)
            throws Exception {
        return selectOrm(ormClass, sqlQuery, solumnFilter, new FilterMap(filterMap));
    }

    public <OrmType> OrmType selectOrm(Class<OrmType> ormClass, String sqlQuery, IFilterValues filters) throws Exception {
        return selectOrm(ormClass, sqlQuery, ALL_COLUMNS_FILTER, filters);
    }

    public <OrmType> OrmType selectSimpleOrm(Class<OrmType> ormClass, String sqlQuery, Object... filters) throws Exception {
        return selectOrm(ormClass, sqlQuery, ALL_COLUMNS_FILTER, new FilterSequence(filters));
    }

    public <OrmType> OrmType selectSimpleOrm(Class<OrmType> ormClass, String sqlQuery, ISelectedColumnFilter solumnFilter, Object... filters)
            throws Exception {
        return selectOrm(ormClass, sqlQuery, solumnFilter, new FilterSequence(filters));
    }

    public <OrmType> OrmType selectOrm(Class<OrmType> ormClass, String sqlQuery, ISelectedColumnFilter solumnFilter, IFilterValues filters)
            throws Exception {
        IOrmDaoAdapter daoAdapter = createDao(connectionHolder, sqlQuery);
        daoAdapter.setSelectedColumnFilter(solumnFilter);
        daoAdapter.setFilterValue(filters);
        return daoAdapter.load(ormClass);
    }

    public <OrmType> List<OrmType> selectOrms(Class<OrmType> ormClass, String sqlQuery) throws Exception {
        return selectOrms(ormClass, sqlQuery, ALL_COLUMNS_FILTER, EMPTY_FILTER_MAP);
    }

    public <OrmType> List<OrmType> selectOrms(Class<OrmType> ormClass, String sqlQuery, ISelectedColumnFilter solumnFilter) throws Exception {
        return selectOrms(ormClass, sqlQuery, solumnFilter, EMPTY_FILTER_MAP);
    }

    public <OrmType> List<OrmType> selectOrms(Class<OrmType> ormClass, String sqlQuery, Object filterBobj) throws Exception {
        return selectOrms(ormClass, sqlQuery, ALL_COLUMNS_FILTER, new FilterObject(filterBobj));
    }

    public <OrmType> List<OrmType> selectOrms(Class<OrmType> ormClass, String sqlQuery, Map<String, Object> filterMap) throws Exception {
        return selectOrms(ormClass, sqlQuery, ALL_COLUMNS_FILTER, new FilterMap(filterMap));
    }

    public <OrmType> List<OrmType> selectOrms(Class<OrmType> ormClass, String sqlQuery, IFilterValues filter) throws Exception {
        return selectOrms(ormClass, sqlQuery, ALL_COLUMNS_FILTER, filter);
    }

    public <OrmType> List<OrmType> selectOrms(Class<OrmType> ormClass, String sqlQuery, ISelectedColumnFilter solumnFilter, Object filterBobj)
            throws Exception {
        return selectOrms(ormClass, sqlQuery, solumnFilter, new FilterObject(filterBobj));
    }

    public <OrmType> List<OrmType> selectOrms(Class<OrmType> ormClass, String sqlQuery, ISelectedColumnFilter solumnFilter, Map<String, Object> filterMap)
            throws Exception {
        return selectOrms(ormClass, sqlQuery, solumnFilter, new FilterMap(filterMap));
    }

    public <OrmType> List<OrmType> selectSimpleOrms(Class<OrmType> ormClass, String sqlQuery, Object... filters) throws Exception {
        return selectOrms(ormClass, sqlQuery, ALL_COLUMNS_FILTER, new FilterSequence(filters));
    }

    public <OrmType> List<OrmType> selectSimpleOrms(Class<OrmType> ormClass, String sqlQuery, ISelectedColumnFilter solumnFilter,
            Object... filters) throws Exception {
        return selectOrms(ormClass, sqlQuery, solumnFilter, new FilterSequence(filters));
    }

    public <OrmType> List<OrmType> selectOrms(Class<OrmType> ormClass, String sqlQuery, ISelectedColumnFilter solumnFilter,
            IFilterValues filters) throws Exception {
        IOrmDaoAdapter daoAdapter = createDao(connectionHolder, sqlQuery);
        daoAdapter.setSelectedColumnFilter(solumnFilter);
        daoAdapter.setFilterValue(filters);
        return daoAdapter.loads(ormClass);
    }

    public <OrmType> void handleSelectedOrms(Class<OrmType> ormClass, String sqlQuery, OrmHandler<OrmType> handler, Object filterBobj)
            throws Exception {
        handleSelectedOrms(ormClass, sqlQuery, handler, ALL_COLUMNS_FILTER, new FilterObject(filterBobj));
    }

    public <OrmType> void handleSelectedOrms(Class<OrmType> ormClass, String sqlQuery, OrmHandler<OrmType> handler, Map<String, Object> filterMap)
            throws Exception {
        handleSelectedOrms(ormClass, sqlQuery, handler, ALL_COLUMNS_FILTER, new FilterMap(filterMap));
    }

    public <OrmType> void handleSelectedOrms(Class<OrmType> ormClass, String sqlQuery, OrmHandler<OrmType> handler, IFilterValues filter)
            throws Exception {
        handleSelectedOrms(ormClass, sqlQuery, handler, ALL_COLUMNS_FILTER, filter);
    }

    public <OrmType> void handleSelectedOrms(Class<OrmType> ormClass, String sqlQuery, OrmHandler<OrmType> handler,
            ISelectedColumnFilter solumnFilter, Object filterBobj)
            throws Exception {
        handleSelectedOrms(ormClass, sqlQuery, handler, solumnFilter, new FilterObject(filterBobj));
    }

    public <OrmType> void handleSelectedOrms(Class<OrmType> ormClass, String sqlQuery, OrmHandler<OrmType> handler,
            ISelectedColumnFilter solumnFilter, Map<String, Object> filterMap)
            throws Exception {
        handleSelectedOrms(ormClass, sqlQuery, handler, solumnFilter, new FilterMap(filterMap));
    }

    public <OrmType> void handleSelectedOrms(Class<OrmType> ormClass, String sqlQuery, OrmHandler<OrmType> handler) throws Exception {
        handleSelectedOrms(ormClass, sqlQuery, handler, ALL_COLUMNS_FILTER, EMPTY_FILTER_MAP);
    }

    public <OrmType> void handleSelectedOrms(Class<OrmType> ormClass, String sqlQuery, OrmHandler<OrmType> handler,
            ISelectedColumnFilter solumnFilter) throws Exception {
        handleSelectedOrms(ormClass, sqlQuery, handler, solumnFilter, EMPTY_FILTER_MAP);
    }

    public <OrmType> void handleSelectedSimpleOrms(Class<OrmType> ormClass, String sqlQuery, OrmHandler<OrmType> handler, Object... filters)
            throws Exception {
        handleSelectedOrms(ormClass, sqlQuery, handler, ALL_COLUMNS_FILTER, new FilterSequence(filters));
    }

    public <OrmType> void handleSelectedSimpleOrms(Class<OrmType> ormClass, String sqlQuery, OrmHandler<OrmType> handler,
            ISelectedColumnFilter solumnFilter, Object... filters)
            throws Exception {
        handleSelectedOrms(ormClass, sqlQuery, handler, solumnFilter, new FilterSequence(filters));
    }

    public <OrmType> void handleSelectedOrms(Class<OrmType> ormClass, String sqlQuery, OrmHandler<OrmType> handler,
            ISelectedColumnFilter solumnFilter, IFilterValues filters)
            throws Exception {
        IOrmDaoAdapter daoAdapter = createDao(connectionHolder, sqlQuery);
        daoAdapter.setSelectedColumnFilter(solumnFilter);
        daoAdapter.setFilterValue(filters);
        daoAdapter.handle(ormClass, handler);
    }

    public <OrmType> OrmIterator<OrmType> selectOrmIterator(Class<OrmType> ormClass, String sqlQuery, Object filterBobj) throws Exception {
        return selectOrmIterator(ormClass, sqlQuery, ALL_COLUMNS_FILTER, new FilterObject(filterBobj));
    }

    public <OrmType> OrmIterator<OrmType> selectOrmIterator(Class<OrmType> ormClass, String sqlQuery, Map<String, Object> filterMap) throws Exception {
        return selectOrmIterator(ormClass, sqlQuery, ALL_COLUMNS_FILTER, new FilterMap(filterMap));
    }

    public <OrmType> OrmIterator<OrmType> selectOrmIterator(Class<OrmType> ormClass, String sqlQuery, IFilterValues filter) throws Exception {
        return selectOrmIterator(ormClass, sqlQuery, ALL_COLUMNS_FILTER, filter);
    }

    public <OrmType> OrmIterator<OrmType> selectOrmIterator(Class<OrmType> ormClass, String sqlQuery, ISelectedColumnFilter solumnFilter,
            Object filterBobj) throws Exception {
        return selectOrmIterator(ormClass, sqlQuery, solumnFilter, new FilterObject(filterBobj));
    }

    public <OrmType> OrmIterator<OrmType> selectOrmIterator(Class<OrmType> ormClass, String sqlQuery, ISelectedColumnFilter solumnFilter,
            Map<String, Object> filterMap) throws Exception {
        return selectOrmIterator(ormClass, sqlQuery, solumnFilter, new FilterMap(filterMap));
    }

    public <OrmType> OrmIterator<OrmType> selectOrmIterator(Class<OrmType> ormClass, String sqlQuery) throws Exception {
        return selectOrmIterator(ormClass, sqlQuery, ALL_COLUMNS_FILTER, EMPTY_FILTER_MAP);
    }

    public <OrmType> OrmIterator<OrmType> selectOrmIterator(Class<OrmType> ormClass, String sqlQuery, ISelectedColumnFilter solumnFilter)
            throws Exception {
        return selectOrmIterator(ormClass, sqlQuery, solumnFilter, EMPTY_FILTER_MAP);
    }

    public <OrmType> OrmIterator<OrmType> selectSimpleOrmIterator(Class<OrmType> ormClass, String sqlQuery, Object... filters)
            throws Exception {
        return selectOrmIterator(ormClass, sqlQuery, ALL_COLUMNS_FILTER, new FilterSequence(filters));
    }

    public <OrmType> OrmIterator<OrmType> selectSimpleOrmIterator(Class<OrmType> ormClass, String sqlQuery,
            ISelectedColumnFilter solumnFilter, Object... filters)
            throws Exception {
        return selectOrmIterator(ormClass, sqlQuery, solumnFilter, new FilterSequence(filters));
    }

    public <OrmType> OrmIterator<OrmType> selectOrmIterator(Class<OrmType> ormClass, String sqlQuery, ISelectedColumnFilter solumnFilter,
            IFilterValues filters) throws Exception {
        IOrmDaoAdapter daoAdapter = createDao(connectionHolder, sqlQuery);
        daoAdapter.setSelectedColumnFilter(solumnFilter);
        daoAdapter.setFilterValue(filters);
        return daoAdapter.iterate(ormClass);
    }

    public int updateOrm(String sqlQuery, Object updateBobj) throws Exception {
        return updateOrm(sqlQuery, new UpdateObject(updateBobj), EMPTY_FILTER_MAP);
    }

    public int updateOrm(String sqlQuery, IUpdateValues updateValues) throws Exception {
        return updateOrm(sqlQuery, updateValues, EMPTY_FILTER_MAP);
    }

    public int updateOrm(String sqlQuery, Map<String, Object> updateMap) throws Exception {
        return updateOrm(sqlQuery, new UpdateMap(updateMap), EMPTY_FILTER_MAP);
    }

    public int updateOrm(String sqlQuery, Object updateBobj, Object filterBobj) throws Exception {
        return updateOrm(sqlQuery, new UpdateObject(updateBobj), new FilterObject(filterBobj));
    }

    public int updateOrm(String sqlQuery, Object updateBobj, Map<String, Object> filterMap) throws Exception {
        return updateOrm(sqlQuery, new UpdateObject(updateBobj), new FilterMap(filterMap));
    }

    public int updateOrm(String sqlQuery, Map<String, Object> updateMap, Map<String, Object> filterMap) throws Exception {
        return updateOrm(sqlQuery, new UpdateMap(updateMap), new FilterMap(filterMap));
    }

    public int updateSimpleOrm(String sqlQuery, Object ... values) throws Exception {
        return updateOrm(sqlQuery, new UpdateSequence(values), EMPTY_FILTER_MAP);
    }

    public int updateOrm(String sqlQuery, IUpdateValues updateValues, IFilterValues filters) throws Exception {
        IOrmDaoAdapter daoAdapter = createDao(connectionHolder, sqlQuery);
        daoAdapter.setUpdateValue(updateValues);
        daoAdapter.setFilterValue(filters);
        return daoAdapter.update();
    }

    public static IOrmDaoAdapter createDao(Object connectionHolder, String sqlQuery) {
        return new OrmDaoAdapter(connectionHolder, sqlQuery);
    }

}
