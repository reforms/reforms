package com.reforms.orm;

import static com.reforms.orm.filter.FilterMap.EMPTY_FILTER_MAP;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import com.reforms.orm.extractor.SelectColumnExtractorAndAliasModifier;
import com.reforms.orm.filter.*;
import com.reforms.orm.select.SelectedColumn;
import com.reforms.orm.select.report.ResultSetRecordReader;
import com.reforms.orm.select.report.model.Report;
import com.reforms.orm.select.report.model.ReportIterator;
import com.reforms.orm.select.report.model.ReportRecord;
import com.reforms.orm.select.report.model.ReportRecordHandler;
import com.reforms.sql.expr.query.SelectQuery;
import com.reforms.sql.parser.SqlParser;

/**
 * TODO фитчи - проработать вопрос:
 *  - с заголовком отчета
 *  - с постраничной разбивкой
 *  - с сортировкой по требованию
 * @author evgenie
 *
 */
public class ReportDao {

    public Report loadReport(Object connectionHolder, String sqlQuery) throws Exception {
        return loadReport(connectionHolder, sqlQuery, EMPTY_FILTER_MAP);
    }

    public Report loadReport(Object connectionHolder, String sqlQuery, Object filterBobj) throws Exception {
        return loadReport(connectionHolder, sqlQuery, new FilterObject(filterBobj));
    }

    /**
     * Не рекомендуется использовать при наличии дианмических фильтров
     * @param connectionHolder
     * @param sqlQuery
     * @param filters
     * @return
     * @throws Exception
     */
    public Report loadSimpleReport(Object connectionHolder, String sqlQuery, Object... filters) throws Exception {
        return loadReport(connectionHolder, sqlQuery, new FilterSequence(filters));
    }

    public Report loadReport(Object connectionHolder, String sqlQuery, FilterValues filters) throws Exception {
        OrmContext rCtx = OrmConfigurator.get(OrmContext.class);
        IConnectionHolder cHolder = rCtx.getConnectionHolder();
        Connection connection = cHolder.getConnection(connectionHolder);
        SelectQuery selectQuery = parseSqlQuery(sqlQuery);
        SelectColumnExtractorAndAliasModifier selectedColumnExtractor = new SelectColumnExtractorAndAliasModifier();
        List<SelectedColumn> selectedColumns = selectedColumnExtractor.extractSelectedColumns(selectQuery);
        ResultSetRecordReader rsReader = new ResultSetRecordReader(selectedColumns, rCtx);
        Report report = new Report();
        SelectQueryFilterPreparer filterPreparer = new SelectQueryFilterPreparer();
        FilterPrepareStatementSetter paramSetterEngine = filterPreparer.prepareFilters(selectQuery, filters);
        String preparedSqlQuery = selectQuery.toString();
        try (PreparedStatement ps = connection.prepareStatement(preparedSqlQuery)) {
            paramSetterEngine.setParamsTo(ps);
            try (ResultSet rs = ps.executeQuery()) {
                ReportRecord reportRecord = null;
                while ((reportRecord = rsReader.read(rs)) != null) {
                    report.add(reportRecord);
                }
            }
        }
        return report;
    }

    public void handleReport(Object connectionHolder, String sqlQuery, ReportRecordHandler handler) throws Exception {
        handleReport(connectionHolder, sqlQuery, handler, EMPTY_FILTER_MAP);
    }

    public void handleReport(Object connectionHolder, String sqlQuery, ReportRecordHandler handler, Object filterBobj) throws Exception {
        handleReport(connectionHolder, sqlQuery, handler, new FilterObject(filterBobj));
    }

    public void handleSimpleReport(Object connectionHolder, String sqlQuery, ReportRecordHandler handler, Object... filters)
            throws Exception {
        handleReport(connectionHolder, sqlQuery, handler, new FilterSequence(filters));
    }

    public void handleReport(Object connectionHolder, String sqlQuery, ReportRecordHandler handler, FilterValues filters) throws Exception {
        OrmContext rCtx = OrmConfigurator.get(OrmContext.class);
        IConnectionHolder cHolder = rCtx.getConnectionHolder();
        Connection connection = cHolder.getConnection(connectionHolder);
        SelectQuery selectQuery = parseSqlQuery(sqlQuery);
        SelectColumnExtractorAndAliasModifier selectedColumnExtractor = new SelectColumnExtractorAndAliasModifier();
        List<SelectedColumn> selectedColumns = selectedColumnExtractor.extractSelectedColumns(selectQuery);
        ResultSetRecordReader rsReader = new ResultSetRecordReader(selectedColumns, rCtx);
        SelectQueryFilterPreparer filterPreparer = new SelectQueryFilterPreparer();
        FilterPrepareStatementSetter paramSetterEngine = filterPreparer.prepareFilters(selectQuery, filters);
        String preparedSqlQuery = selectQuery.toString();
        try (PreparedStatement ps = connection.prepareStatement(preparedSqlQuery)) {
            paramSetterEngine.setParamsTo(ps);
            try (ResultSet rs = ps.executeQuery()) {
                ReportRecord reportRecord = null;
                handler.startHandle();
                while ((reportRecord = rsReader.read(rs)) != null) {
                    handler.handleReportRecord(reportRecord);
                }
                handler.endHandle();
            }
        }
    }

    public ReportIterator loadReportIterator(Object connectionHolder, String sqlQuery) throws Exception {
        return loadReportIterator(connectionHolder, sqlQuery, EMPTY_FILTER_MAP);
    }

    public ReportIterator loadReportIterator(Object connectionHolder, String sqlQuery, Object filterBobj) throws Exception {
        return loadReportIterator(connectionHolder, sqlQuery, new FilterObject(filterBobj));
    }

    public ReportIterator loadSimpleReportIterator(Object connectionHolder, String sqlQuery, Object... filters) throws Exception {
        return loadReportIterator(connectionHolder, sqlQuery, new FilterSequence(filters));
    }

    public ReportIterator loadReportIterator(Object connectionHolder, String sqlQuery, FilterValues filters) throws Exception {
        OrmContext rCtx = OrmConfigurator.get(OrmContext.class);
        IConnectionHolder cHolder = rCtx.getConnectionHolder();
        Connection connection = cHolder.getConnection(connectionHolder);
        SelectQuery selectQuery = parseSqlQuery(sqlQuery);
        SelectColumnExtractorAndAliasModifier selectedColumnExtractor = new SelectColumnExtractorAndAliasModifier();
        List<SelectedColumn> selectedColumns = selectedColumnExtractor.extractSelectedColumns(selectQuery);
        ResultSetRecordReader rsReader = new ResultSetRecordReader(selectedColumns, rCtx);
        SelectQueryFilterPreparer filterPreparer = new SelectQueryFilterPreparer();
        FilterPrepareStatementSetter paramSetterEngine = filterPreparer.prepareFilters(selectQuery, filters);
        String preparedSqlQuery = selectQuery.toString();
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(preparedSqlQuery);
            paramSetterEngine.setParamsTo(ps);
            ReportIterator reportIterator = new ReportIterator(ps, rsReader);
            reportIterator.prepare();
            return reportIterator;
        } catch (Exception ex) {
            if (ps != null) {
                ps.close();
            }
            throw ex;
        }
    }

    private SelectQuery parseSqlQuery(String sqlQuery) {
        SqlParser sqlParser = new SqlParser(sqlQuery);
        SelectQuery selectQuery = sqlParser.parseSelectQuery();
        return selectQuery;
    }

}
