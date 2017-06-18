package com.reforms.orm.dao;

import java.util.*;

import com.reforms.orm.dao.bobj.IOrmDaoAdapter;
import com.reforms.orm.dao.bobj.model.OrmHandler;
import com.reforms.orm.dao.bobj.model.OrmIterator;
import com.reforms.orm.dao.bobj.update.*;
import com.reforms.orm.dao.filter.FilterMap;
import com.reforms.orm.dao.filter.FilterObject;
import com.reforms.orm.dao.filter.FilterSequence;
import com.reforms.orm.dao.filter.IFilterValues;
import com.reforms.orm.dao.filter.column.CompositeSelectedColumnFilter;
import com.reforms.orm.dao.filter.column.ISelectedColumnFilter;
import com.reforms.orm.dao.filter.column.IndexSelectFilter;
import com.reforms.orm.dao.paging.IPageFilter;
import com.reforms.orm.dao.paging.PageFilter;

/**
 * Адаптер к dao
 * @author evgenie
 */
public class OrmDaoAdapter implements IOrmDaoAdapter {

    // base info
    private final Object connectionHolder;
    private final String query;

    // selected info
    private List<Integer> selectedColumnIndexes;
    private ISelectedColumnFilter selectedColumnFilter;

    // filters info
    private List<Object> simpleFilterValues;
    private Object filterBobj;
    private FilterMap filterMap;
    private IFilterValues filter;

    // paging info
    private Integer pageLimit;
    private Integer pageOffset;
    private IPageFilter pageFilter;

    // updates OR insert info
    private List<Object> simpleUpdateValues;
    private Object updateBobj;
    private UpdateMap updateMap;
    private IUpdateValues updateValues;
    private Iterator<IUpdateValues> batchUpdateValues;

    public OrmDaoAdapter(Object connectionHolder, String query) {
        this.connectionHolder = connectionHolder;
        this.query = query;
    }

    @Override
    public IOrmDaoAdapter addSelectableIndex(int toBeSelectedIndexColumn) {
        addIndex(toBeSelectedIndexColumn);
        return this;
    }

    @Override
    public IOrmDaoAdapter addSelectableIndexes(int... toBeSelectedIndexColumns) {
        for (int toBeSelectedIndexColumn : toBeSelectedIndexColumns) {
            addIndex(toBeSelectedIndexColumn);
        }
        return this;
    }

    private void addIndex(int index) {
        if (selectedColumnIndexes == null) {
            selectedColumnIndexes = new ArrayList<>();
        }
        if (!selectedColumnIndexes.contains(index)) {
            selectedColumnIndexes.add(index);
        }
    }

    @Override
    public IOrmDaoAdapter setSelectedColumnFilter(ISelectedColumnFilter filter) {
        selectedColumnFilter = filter;
        return this;
    }

    @Override
    public IOrmDaoAdapter addSimpleFilterValue(Object value) {
        addFilterValue(value);
        return this;
    }

    @Override
    public IOrmDaoAdapter addSimpleFilterValues(Object... values) {
        for (Object value : values) {
            addFilterValue(value);
        }
        return this;
    }

    @Override
    public IOrmDaoAdapter setBatchUpdateValues(Iterator<IUpdateValues> updateValues) {
        this.batchUpdateValues = updateValues;
        return this;
    }

    private void addFilterValue(Object simpleValue) {
        if (simpleFilterValues == null) {
            simpleFilterValues = new ArrayList<>();
        }
        simpleFilterValues.add(simpleValue);
    }

    @Override
    public IOrmDaoAdapter setFilterObject(Object filterBobj) {
        this.filterBobj = filterBobj;
        return this;
    }

    @Override
    public IOrmDaoAdapter addFilterPair(String paramName, Object paramValue) {
        if (filterMap == null) {
            filterMap = new FilterMap();
        }
        filterMap.putValue(paramName, paramValue);
        return this;
    }

    @Override
    public IOrmDaoAdapter addFilterPairs(Map<String, Object> filterValues) {
        if (filterMap == null) {
            filterMap = new FilterMap();
        }
        filterMap.putValues(filterValues);
        return this;
    }

    @Override
    public IOrmDaoAdapter setFilterValue(IFilterValues filter) {
        this.filter = filter;
        return this;
    }

    @Override
    public IOrmDaoAdapter setPageLimit(int pageLimit) {
        this.pageLimit = pageLimit;
        return this;
    }

    @Override
    public IOrmDaoAdapter setPageOffset(int pageOffset) {
        this.pageOffset = pageOffset;
        return this;
    }

    @Override
    public IOrmDaoAdapter setPageOffset(IPageFilter pageFilter) {
        this.pageFilter = pageFilter;
        return this;
    }

    @Override
    public IOrmDaoAdapter addUpdateValue(Object updateValue) {
        return addUpdateValue0(updateValue);
    }

    @Override
    public IOrmDaoAdapter addUpdateValues(Object ... updateValues) {
        for (Object updateValue : updateValues) {
            addUpdateValue0(updateValue);
        }
        return this;
    }

    private IOrmDaoAdapter addUpdateValue0(Object updateValue) {
        if (simpleUpdateValues == null) {
            simpleUpdateValues = new ArrayList<>();
        }
        simpleUpdateValues.add(updateValue);
        return this;
    }

    @Override
    public IOrmDaoAdapter setUpdateObject(Object updateBobj) {
        this.updateBobj = updateBobj;
        return this;
    }

    @Override
    public IOrmDaoAdapter addUpdatePair(String paramName, Object updateValue) {
        if (updateMap == null) {
            updateMap = new UpdateMap();
        }
        updateMap.putValue(paramName, updateValue);
        return this;
    }

    @Override
    public IOrmDaoAdapter addUpdatePairs(Map<String, Object> updateValues) {
        if (updateMap == null) {
            updateMap = new UpdateMap();
        }
        updateMap.putValues(updateValues);
        return this;
    }

    @Override
    public IOrmDaoAdapter setUpdateValue(IUpdateValues updateValues) {
        this.updateValues = updateValues;
        return this;
    }


    @Override
    public IOrmDaoAdapter addInsertValue(Object insertValue) {
        return addUpdateValue(insertValue);
    }

    @Override
    public IOrmDaoAdapter addInsertValues(Object... insertValues) {
        return addUpdateValues(insertValues);
    }

    @Override
    public IOrmDaoAdapter setInsertObject(Object insertBobj) {
        return setUpdateObject(insertBobj);
    }

    @Override
    public IOrmDaoAdapter addInsertPair(String paramName, Object insertValue) {
        return addUpdatePair(paramName, insertValue);
    }

    @Override
    public IOrmDaoAdapter addInsertPairs(Map<String, Object> insertValues) {
        return addUpdatePairs(insertValues);
    }

    @Override
    public IOrmDaoAdapter setInsertValue(IInsertValues insertValues) {
        if (insertValues instanceof IUpdateValues) {
            return setUpdateValue((IUpdateValues) insertValues);
        }
        throw new IllegalStateException("Неизвестная реализация '" + IInsertValues.class + "'");
    }

    private ISelectedColumnFilter buildSelectedColumnFilter() {
        ISelectedColumnFilter firstFilter = null;
        if (selectedColumnIndexes != null) {
            firstFilter = new IndexSelectFilter(selectedColumnIndexes);
        }
        ISelectedColumnFilter secondFilter = selectedColumnFilter;
        if (secondFilter == null) {
            return firstFilter;
        }
        if (firstFilter == null) {
            return secondFilter;
        }
        return new CompositeSelectedColumnFilter(firstFilter, secondFilter);
    }

    private IFilterValues buildFilterValues() {
        IPageFilter pageFilter = buildPageFilter();
        if (simpleFilterValues != null) {
            if (pageFilter != null) {
                simpleFilterValues.add(pageFilter);
            }
            FilterSequence filterValues = new FilterSequence(simpleFilterValues.toArray());
            return filterValues;
        }
        if (filterBobj != null) {
            FilterObject filterValues = new FilterObject(filterBobj, pageFilter);
            return filterValues;
        }
        if (filterMap != null) {
            filterMap.setPageFilter(pageFilter);
            return filterMap;
        }
        return filter;
    }

    private IUpdateValues buildUpdateValues() {
        if (simpleUpdateValues != null) {
            UpdateSequence updateValues = new UpdateSequence(simpleUpdateValues.toArray());
            return updateValues;
        }
        if (updateBobj != null) {
            UpdateObject updateValues = new UpdateObject(updateBobj);
            return updateValues;
        }
        if (updateMap != null) {
            return updateMap;
        }
        return updateValues;
    }

    private IInsertValues buildInsertValues() {
        return buildUpdateValues();
    }

    private IPageFilter buildPageFilter() {
        if (pageLimit != null || pageOffset != null) {
            return new PageFilter(pageLimit, pageOffset);
        }
        return pageFilter;
    }

    private DaoSelectContext buildDaoSelectContext(Class<?> ormClass) {
        DaoSelectContext daoCtx = new DaoSelectContext();
        daoCtx.setConnectionHolder(connectionHolder);
        daoCtx.setQuery(query);
        daoCtx.setOrmType(ormClass);
        daoCtx.setSelectedColumnFilter(buildSelectedColumnFilter());
        daoCtx.setFilterValues(buildFilterValues());
        return daoCtx;
    }

    private DaoUpdateContext buildDaoUpdateContext() {
        DaoUpdateContext daoCtx = new DaoUpdateContext();
        daoCtx.setConnectionHolder(connectionHolder);
        daoCtx.setQuery(query);
        daoCtx.setUpateValues(buildUpdateValues());
        daoCtx.setFilterValues(buildFilterValues());
        return daoCtx;
    }

    private DaoBatchUpdateContext buildDaoBatchUpdateContext(int batchSize) {
        DaoBatchUpdateContext daoCtx = new DaoBatchUpdateContext();
        daoCtx.setConnectionHolder(connectionHolder);
        daoCtx.setQuery(query);
        daoCtx.setBatchSize(batchSize);
        daoCtx.setUpateValues(batchUpdateValues);
        return daoCtx;
    }

    private DaoDeleteContext buildDaoDeleteContext() {
        DaoDeleteContext daoCtx = new DaoDeleteContext();
        daoCtx.setConnectionHolder(connectionHolder);
        daoCtx.setQuery(query);
        daoCtx.setFilterValues(buildFilterValues());
        return daoCtx;
    }

    private DaoInsertContext buildDaoInsertContext() {
        DaoInsertContext daoCtx = new DaoInsertContext();
        daoCtx.setConnectionHolder(connectionHolder);
        daoCtx.setQuery(query);
        daoCtx.setInsertValues(buildInsertValues());
        return daoCtx;
    }

    @Override
    public <OrmType> OrmType load(Class<OrmType> ormClass) throws Exception {
        IOrmDao dao = new OrmDao();
        DaoSelectContext daoCtx = buildDaoSelectContext(ormClass);
        return dao.load(daoCtx);
    }

    @Override
    public <OrmType> List<OrmType> loads(Class<OrmType> ormClass) throws Exception {
        IOrmDao dao = new OrmDao();
        DaoSelectContext daoCtx = buildDaoSelectContext(ormClass);
        return dao.loads(daoCtx);
    }

    @Override
    public <OrmType> Set<OrmType> set(Class<OrmType> ormClass) throws Exception {
        IOrmDao dao = new OrmDao();
        DaoSelectContext daoCtx = buildDaoSelectContext(ormClass);
        return dao.set(daoCtx);
    }

    @Override
    public <OrmType> OrmIterator<OrmType> iterate(Class<OrmType> ormClass) throws Exception {
        IOrmDao dao = new OrmDao();
        DaoSelectContext daoCtx = buildDaoSelectContext(ormClass);
        return dao.iterate(daoCtx);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <OrmType> void handle(Class<OrmType> ormClass, OrmHandler<OrmType> handler) throws Exception {
        IOrmDao dao = new OrmDao();
        DaoSelectContext daoCtx = buildDaoSelectContext(ormClass);
        dao.handle(daoCtx, (OrmHandler<Object>) handler);
    }

    @Override
    public int update() throws Exception {
        IOrmDao dao = new OrmDao();
        DaoUpdateContext daoCtx = buildDaoUpdateContext();
        return dao.update(daoCtx);
    }

    @Override
    public int[][] updates(int batchSize) throws Exception {
        IOrmDao dao = new OrmDao();
        DaoBatchUpdateContext daoCtx = buildDaoBatchUpdateContext(batchSize);
        return dao.updates(daoCtx);
    }

    @Override
    public int delete() throws Exception {
        IOrmDao dao = new OrmDao();
        DaoDeleteContext daoCtx = buildDaoDeleteContext();
        return dao.delete(daoCtx);
    }

    @Override
    public void insert() throws Exception {
        IOrmDao dao = new OrmDao();
        DaoInsertContext daoCtx = buildDaoInsertContext();
        dao.insert(daoCtx);
    }

}
