package com.reforms.orm.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.reforms.orm.dao.adapter.IDaoAdapter;
import com.reforms.orm.filter.*;
import com.reforms.orm.selectable.CompositeSelectedColumnFilter;
import com.reforms.orm.selectable.ISelectedColumnFilter;
import com.reforms.orm.selectable.IndexSelectFilter;

/**
 * Адаптер к dao
 * @author evgenie
 */
public class OrmDaoAdapter implements IDaoAdapter {

    private final Object connectionHolder;
    private final String query;

    private List<Integer> selectedColumnIndexes;
    private ISelectedColumnFilter selectedColumnFilter;

    private List<Object> simpleFilterValues;
    private Object filterBobj;
    private FilterMap filterMap;
    private IFilterValues filter;

    private Integer pageLimit;
    private Integer pageOffset;
    private IPageFilter pageFilter;

    public OrmDaoAdapter(Object connectionHolder, String query) {
        this.connectionHolder = connectionHolder;
        this.query = query;
    }

    @Override
    public IDaoAdapter addSelectableIndex(int toBeSelectedIndexColumn) {
        addIndex(toBeSelectedIndexColumn);
        return this;
    }

    @Override
    public IDaoAdapter addSelectableIndexes(int... toBeSelectedIndexColumns) {
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
    public IDaoAdapter setSelectedColumnFilter(ISelectedColumnFilter filter) {
        selectedColumnFilter = filter;
        return this;
    }

    @Override
    public IDaoAdapter addSimpleFilterValue(Object value) {
        addFilterValue(value);
        return this;
    }

    @Override
    public IDaoAdapter addSimpleFilterValues(Object... values) {
        for (Object value : values) {
            addFilterValue(value);
        }
        return this;
    }

    private void addFilterValue(Object simpleValue) {
        if (simpleFilterValues == null) {
            simpleFilterValues = new ArrayList<>();
        }
        simpleFilterValues.add(simpleValue);
    }

    @Override
    public IDaoAdapter setFilterObject(Object filterBobj) {
        this.filterBobj = filterBobj;
        return this;
    }

    @Override
    public IDaoAdapter addFilterPair(String paramName, Object paramValue) {
        if (filterMap == null) {
            filterMap = new FilterMap();
        }
        filterMap.putValue(paramName, paramValue);
        return this;
    }

    @Override
    public IDaoAdapter addFilterPairs(Map<String, Object> filterValues) {
        if (filterMap == null) {
            filterMap = new FilterMap();
        }
        filterMap.putValues(filterValues);
        return this;
    }

    @Override
    public IDaoAdapter setFilterValue(IFilterValues filter) {
        this.filter = filter;
        return this;
    }

    @Override
    public IDaoAdapter setPageLimit(int pageLimit) {
        this.pageLimit = pageLimit;
        return this;
    }

    @Override
    public IDaoAdapter setPageOffset(int pageOffset) {
        this.pageOffset = pageOffset;
        return this;
    }

    @Override
    public IDaoAdapter setPageOffset(IPageFilter pageFilter) {
        this.pageFilter = pageFilter;
        return this;
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
            FilterSequence filterValues = new FilterSequence(simpleFilterValues.toArray());
            if (pageFilter != null) {
                filterValues.applyPageFilter(pageFilter);
            }
            return filterValues;
        }
        if (filterBobj != null) {
            FilterObject filterValues = new FilterObject(filterBobj);
            if (pageFilter != null) {
                filterValues.applyPageFilter(pageFilter);
            }
            return filterValues;
        }
        if (filterMap != null) {
            if (pageFilter != null) {
                filterMap.applyPageFilter(pageFilter);
            }
            return filterMap;
        }
        return filter;
    }

    private IPageFilter buildPageFilter() {
        if (pageLimit != null || pageOffset != null) {
            return new PageFilter(pageLimit, pageOffset);
        }
        return pageFilter;
    }

    private DaoContext buildDaoContext(Class<?> ormClass) {
        DaoContext daoCtx = new DaoContext();
        daoCtx.setConnectionHolder(connectionHolder);
        daoCtx.setQuery(query);
        daoCtx.setOrmType(ormClass);
        daoCtx.setSelectedColumnFilter(buildSelectedColumnFilter());
        daoCtx.setFilterValues(buildFilterValues());
        return daoCtx;
    }

    @Override
    public <OrmType> OrmType load(Class<OrmType> ormClass) throws Exception {
        IOrmDao dao = new OrmDao();
        DaoContext daoCtx = buildDaoContext(ormClass);
        return dao.load(daoCtx);
    }

    @Override
    public <OrmType> List<OrmType> loads(Class<OrmType> ormClass) throws Exception {
        IOrmDao dao = new OrmDao();
        DaoContext daoCtx = buildDaoContext(ormClass);
        return dao.loads(daoCtx);
    }

    @Override
    public <OrmType> OrmIterator<OrmType> iterate(Class<OrmType> ormClass) throws Exception {
        IOrmDao dao = new OrmDao();
        DaoContext daoCtx = buildDaoContext(ormClass);
        return dao.iterate(daoCtx);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <OrmType> void handle(Class<OrmType> ormClass, OrmHandler<OrmType> handler) throws Exception {
        IOrmDao dao = new OrmDao();
        DaoContext daoCtx = buildDaoContext(ormClass);
        dao.handle(daoCtx, (OrmHandler<Object>) handler);
    }
}
