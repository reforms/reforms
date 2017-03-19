package com.reforms.orm;

import static com.reforms.orm.dao.filter.FilterMap.EMPTY_FILTER_MAP;
import static com.reforms.orm.dao.filter.column.AllSelectedColumnFilter.ALL_COLUMNS_FILTER;

import com.reforms.orm.dao.ReportDaoAdapter;
import com.reforms.orm.dao.ReportIterator;
import com.reforms.orm.dao.ReportRecordHandler;
import com.reforms.orm.dao.filter.FilterObject;
import com.reforms.orm.dao.filter.FilterSequence;
import com.reforms.orm.dao.filter.IFilterValues;
import com.reforms.orm.dao.filter.column.ISelectedColumnFilter;
import com.reforms.orm.dao.report.model.Report;

/**
 * TODO фитчи - проработать вопрос:
 *  - с заголовком отчета
 *  - с постраничной разбивкой
 *  - с сортировкой по требованию
 * @author evgenie
 *
 */
public class ReportDao {

    private Object connectionHolder;

    public ReportDao(Object connectionHolder) {
        this.connectionHolder = connectionHolder;
    }

    public Report loadReport(String sqlQuery) throws Exception {
        return loadReport(sqlQuery, ALL_COLUMNS_FILTER, EMPTY_FILTER_MAP);
    }

    public Report loadReport(String sqlQuery, ISelectedColumnFilter solumnFilter) throws Exception {
        return loadReport(sqlQuery, solumnFilter, EMPTY_FILTER_MAP);
    }

    public Report loadReport(String sqlQuery, Object filterBobj) throws Exception {
        return loadReport(sqlQuery, ALL_COLUMNS_FILTER, new FilterObject(filterBobj));
    }

    public Report loadReport(String sqlQuery, IFilterValues filter) throws Exception {
        return loadReport(sqlQuery, ALL_COLUMNS_FILTER, filter);
    }

    /**
     * Не рекомендуется использовать при наличии дианмических фильтров
     * @param connectionHolder
     * @param sqlQuery
     * @param filters
     * @return
     * @throws Exception
     */
    public Report loadSimpleReport(String sqlQuery, Object... filters) throws Exception {
        return loadReport(sqlQuery, ALL_COLUMNS_FILTER, new FilterSequence(filters));
    }

    public Report loadSimpleReport(String sqlQuery, ISelectedColumnFilter solumnFilter, Object... filters) throws Exception {
        return loadReport(sqlQuery, solumnFilter, new FilterSequence(filters));
    }

    public Report loadReport(String sqlQuery, ISelectedColumnFilter solumnFilter, IFilterValues filters) throws Exception {
        ReportDaoAdapter reportAdapter = new ReportDaoAdapter(connectionHolder, sqlQuery);
        return reportAdapter.setSelectedColumnFilter(solumnFilter).setFilterValue(filters).loadReport();
    }

    public void handleReport(String sqlQuery, ReportRecordHandler handler) throws Exception {
        handleReport(sqlQuery, handler, ALL_COLUMNS_FILTER, EMPTY_FILTER_MAP);
    }

    public void handleReport(String sqlQuery, ReportRecordHandler handler, ISelectedColumnFilter solumnFilter) throws Exception {
        handleReport(sqlQuery, handler, solumnFilter, EMPTY_FILTER_MAP);
    }

    public void handleReport(String sqlQuery, ReportRecordHandler handler, Object filterBobj) throws Exception {
        handleReport(sqlQuery, handler, ALL_COLUMNS_FILTER, new FilterObject(filterBobj));
    }

    public void handleReport(String sqlQuery, ReportRecordHandler handler, IFilterValues filter) throws Exception {
        handleReport(sqlQuery, handler, ALL_COLUMNS_FILTER, filter);
    }

    public void handleReport(String sqlQuery, ReportRecordHandler handler, ISelectedColumnFilter solumnFilter, Object filterBobj) throws Exception {
        handleReport(sqlQuery, handler, solumnFilter, new FilterObject(filterBobj));
    }

    public void handleSimpleReport(String sqlQuery, ReportRecordHandler handler, Object... filters) throws Exception {
        handleReport(sqlQuery, handler, ALL_COLUMNS_FILTER, new FilterSequence(filters));
    }

    public void handleSimpleReport(String sqlQuery, ReportRecordHandler handler, ISelectedColumnFilter solumnFilter, Object... filters) throws Exception {
        handleReport(sqlQuery, handler, solumnFilter, new FilterSequence(filters));
    }

    public void handleReport(String sqlQuery, ReportRecordHandler handler, ISelectedColumnFilter solumnFilter, IFilterValues filters) throws Exception {
        ReportDaoAdapter reportAdapter = new ReportDaoAdapter(connectionHolder, sqlQuery);
        reportAdapter.setSelectedColumnFilter(solumnFilter).setFilterValue(filters).handle(handler);
    }

    public ReportIterator loadReportIterator(String sqlQuery) throws Exception {
        return loadReportIterator(sqlQuery, ALL_COLUMNS_FILTER, EMPTY_FILTER_MAP);
    }

    public ReportIterator loadReportIterator(String sqlQuery, ISelectedColumnFilter solumnFilter) throws Exception {
        return loadReportIterator(sqlQuery, solumnFilter, EMPTY_FILTER_MAP);
    }

    public ReportIterator loadReportIterator(String sqlQuery, Object filterBobj) throws Exception {
        return loadReportIterator(sqlQuery, ALL_COLUMNS_FILTER, new FilterObject(filterBobj));
    }

    public ReportIterator loadReportIterator(String sqlQuery, IFilterValues filter) throws Exception {
        return loadReportIterator(sqlQuery, ALL_COLUMNS_FILTER, filter);
    }

    public ReportIterator loadReportIterator(String sqlQuery, ISelectedColumnFilter solumnFilter, Object filterBobj) throws Exception {
        return loadReportIterator(sqlQuery, solumnFilter, new FilterObject(filterBobj));
    }

    public ReportIterator loadSimpleReportIterator(String sqlQuery, Object... filters) throws Exception {
        return loadReportIterator(sqlQuery, ALL_COLUMNS_FILTER, new FilterSequence(filters));
    }

    public ReportIterator loadSimpleReportIterator(String sqlQuery, ISelectedColumnFilter solumnFilter, Object... filters) throws Exception {
        return loadReportIterator(sqlQuery, solumnFilter, new FilterSequence(filters));
    }

    public ReportIterator loadReportIterator(String sqlQuery, ISelectedColumnFilter solumnFilter, IFilterValues filters) throws Exception {
        ReportDaoAdapter reportAdapter = new ReportDaoAdapter(connectionHolder, sqlQuery);
        return reportAdapter.setSelectedColumnFilter(solumnFilter).setFilterValue(filters).iterate();
    }

}
