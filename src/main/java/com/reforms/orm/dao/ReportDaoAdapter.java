package com.reforms.orm.dao;

import com.reforms.orm.dao.filter.FilterMap;
import com.reforms.orm.dao.filter.FilterObject;
import com.reforms.orm.dao.filter.FilterSequence;
import com.reforms.orm.dao.filter.IFilterValues;
import com.reforms.orm.dao.filter.column.CompositeSelectedColumnFilter;
import com.reforms.orm.dao.filter.column.ISelectedColumnFilter;
import com.reforms.orm.dao.filter.column.IndexSelectFilter;
import com.reforms.orm.dao.filter.page.IPageFilter;
import com.reforms.orm.dao.filter.page.PageFilter;
import com.reforms.orm.dao.report.IReportDaoAdapter;
import com.reforms.orm.dao.report.model.Report;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Адаптер к dao
 * @author evgenie
 */
public class ReportDaoAdapter implements IReportDaoAdapter {

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

    public ReportDaoAdapter(Object connectionHolder, String query) {
        this.connectionHolder = connectionHolder;
        this.query = query;
    }

    @Override
    public IReportDaoAdapter addSelectableIndex(int toBeSelectedIndexColumn) {
        addIndex(toBeSelectedIndexColumn);
        return this;
    }

    @Override
    public IReportDaoAdapter addSelectableIndexes(int... toBeSelectedIndexColumns) {
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
    public IReportDaoAdapter setSelectedColumnFilter(ISelectedColumnFilter filter) {
        selectedColumnFilter = filter;
        return this;
    }

    @Override
    public IReportDaoAdapter addSimpleFilterValue(Object value) {
        addFilterValue(value);
        return this;
    }

    @Override
    public IReportDaoAdapter addSimpleFilterValues(Object... values) {
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
    public IReportDaoAdapter setFilterObject(Object filterBobj) {
        this.filterBobj = filterBobj;
        return this;
    }

    @Override
    public IReportDaoAdapter addFilterPair(String paramName, Object paramValue) {
        if (filterMap == null) {
            filterMap = new FilterMap();
        }
        filterMap.putValue(paramName, paramValue);
        return this;
    }

    @Override
    public IReportDaoAdapter addFilterPairs(Map<String, Object> filterValues) {
        if (filterMap == null) {
            filterMap = new FilterMap();
        }
        filterMap.putValues(filterValues);
        return this;
    }

    @Override
    public IReportDaoAdapter setFilterValue(IFilterValues filter) {
        this.filter = filter;
        return this;
    }

    @Override
    public IReportDaoAdapter setPageLimit(int pageLimit) {
        this.pageLimit = pageLimit;
        return this;
    }

    @Override
    public IReportDaoAdapter setPageOffset(int pageOffset) {
        this.pageOffset = pageOffset;
        return this;
    }

    @Override
    public IReportDaoAdapter setPageOffset(IPageFilter pageFilter) {
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

    private IPageFilter buildPageFilter() {
        if (pageLimit != null || pageOffset != null) {
            return new PageFilter(pageLimit, pageOffset);
        }
        return pageFilter;
    }

    private DaoSelectContext buildDaoContext() {
        DaoSelectContext daoCtx = new DaoSelectContext();
        daoCtx.setConnectionHolder(connectionHolder);
        daoCtx.setQuery(query);
        daoCtx.setSelectedColumnFilter(buildSelectedColumnFilter());
        daoCtx.setFilterValues(buildFilterValues());
        return daoCtx;
    }

    @Override
    public Report loadReport() throws Exception {
        DaoSelectContext daoCtx = buildDaoContext();
        ReportDao reportDao = new ReportDao();
        return reportDao.loadReport(daoCtx);
    }

    @Override
    public ReportIterator iterate() throws Exception {
        DaoSelectContext daoCtx = buildDaoContext();
        ReportDao reportDao = new ReportDao();
        return reportDao.iterate(daoCtx);
    }

    @Override
    public void handle(ReportRecordHandler handler) throws Exception {
        DaoSelectContext daoCtx = buildDaoContext();
        ReportDao reportDao = new ReportDao();
        reportDao.handle(daoCtx, handler);
    }

}
