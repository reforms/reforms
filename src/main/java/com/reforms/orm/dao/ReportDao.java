package com.reforms.orm.dao;

import static com.reforms.orm.OrmConfigurator.getInstance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import com.reforms.ann.ThreadSafe;
import com.reforms.orm.IConnectionHolder;
import com.reforms.orm.OrmConfigurator;
import com.reforms.orm.extractor.ReportSelectColumnExtractorAndAliasModifier;
import com.reforms.orm.filter.FilterPrepareStatementSetter;
import com.reforms.orm.filter.SelectQueryPreparer;
import com.reforms.orm.select.IResultSetObjectReader;
import com.reforms.orm.select.IResultSetReaderFactory;
import com.reforms.orm.select.SelectedColumn;
import com.reforms.orm.select.report.model.Report;
import com.reforms.orm.select.report.model.ReportRecord;
import com.reforms.sql.expr.query.SelectQuery;
import com.reforms.sql.parser.SqlParser;

@ThreadSafe
class ReportDao implements IReportDao {

    @Override
    public Report loadReport(DaoContext daoCtx) throws Exception {
        IConnectionHolder cHolder = getInstance(IConnectionHolder.class);
        Connection connection = cHolder.getConnection(daoCtx.getConnectionHolder());
        SelectQuery selectQuery = parseSqlQuery(daoCtx.getQuery());
        ReportSelectColumnExtractorAndAliasModifier selectedColumnExtractor = OrmConfigurator.getInstance(ReportSelectColumnExtractorAndAliasModifier.class);
        List<SelectedColumn> selectedColumns = selectedColumnExtractor.extractSelectedColumns(selectQuery, daoCtx.getSelectedColumnFilter());
        IResultSetReaderFactory rsrFactory = getInstance(IResultSetReaderFactory.class);
        IResultSetObjectReader reportReader = rsrFactory.resolveReader(ReportRecord.class, selectedColumns);
        Report report = new Report();
        SelectQueryPreparer filterPreparer = OrmConfigurator.getInstance(SelectQueryPreparer.class);
        FilterPrepareStatementSetter paramSetterEngine = filterPreparer.prepare(selectQuery, daoCtx.getFilterValues());
        String preparedSqlQuery = selectQuery.toString();
        try (PreparedStatement ps = connection.prepareStatement(preparedSqlQuery)) {
            paramSetterEngine.setParamsTo(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (reportReader.canRead(rs)) {
                    ReportRecord reportRecord = reportReader.read(rs);
                    report.add(reportRecord);
                }
            }
        }
        return report;
    }

    @Override
    public ReportIterator iterate(DaoContext daoCtx) throws Exception {
        IConnectionHolder cHolder = getInstance(IConnectionHolder.class);
        Connection connection = cHolder.getConnection(daoCtx.getConnectionHolder());
        SelectQuery selectQuery = parseSqlQuery(daoCtx.getQuery());
        ReportSelectColumnExtractorAndAliasModifier selectedColumnExtractor = OrmConfigurator.getInstance(ReportSelectColumnExtractorAndAliasModifier.class);
        List<SelectedColumn> selectedColumns = selectedColumnExtractor.extractSelectedColumns(selectQuery, daoCtx.getSelectedColumnFilter());
        IResultSetReaderFactory rsrFactory = getInstance(IResultSetReaderFactory.class);
        IResultSetObjectReader reportReader = rsrFactory.resolveReader(ReportRecord.class, selectedColumns);
        SelectQueryPreparer filterPreparer = OrmConfigurator.getInstance(SelectQueryPreparer.class);
        FilterPrepareStatementSetter paramSetterEngine = filterPreparer.prepare(selectQuery, daoCtx.getFilterValues());
        String preparedSqlQuery = selectQuery.toString();
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(preparedSqlQuery);
            paramSetterEngine.setParamsTo(ps);
            ReportIterator reportIterator = new ReportIterator(ps, reportReader);
            reportIterator.prepare();
            return reportIterator;
        } catch (Exception ex) {
            if (ps != null) {
                ps.close();
            }
            throw ex;
        }
    }

    @Override
    public void handle(DaoContext daoCtx, ReportRecordHandler handler) throws Exception {
        IConnectionHolder cHolder = getInstance(IConnectionHolder.class);
        Connection connection = cHolder.getConnection(daoCtx.getConnectionHolder());
        SelectQuery selectQuery = parseSqlQuery(daoCtx.getQuery());
        ReportSelectColumnExtractorAndAliasModifier selectedColumnExtractor = OrmConfigurator.getInstance(ReportSelectColumnExtractorAndAliasModifier.class);
        List<SelectedColumn> selectedColumns = selectedColumnExtractor.extractSelectedColumns(selectQuery, daoCtx.getSelectedColumnFilter());
        IResultSetReaderFactory rsrFactory = getInstance(IResultSetReaderFactory.class);
        IResultSetObjectReader reportReader = rsrFactory.resolveReader(ReportRecord.class, selectedColumns);
        SelectQueryPreparer filterPreparer = OrmConfigurator.getInstance(SelectQueryPreparer.class);
        FilterPrepareStatementSetter paramSetterEngine = filterPreparer.prepare(selectQuery, daoCtx.getFilterValues());
        String preparedSqlQuery = selectQuery.toString();
        try (PreparedStatement ps = connection.prepareStatement(preparedSqlQuery)) {
            paramSetterEngine.setParamsTo(ps);
            try (ResultSet rs = ps.executeQuery()) {
                handler.startHandle();
                while (reportReader.canRead(rs)) {
                    ReportRecord reportRecord = reportReader.read(rs);
                    handler.handleReportRecord(reportRecord);
                }
                handler.endHandle();
            }
        }
    }

    private SelectQuery parseSqlQuery(String sqlQuery) {
        SqlParser sqlParser = new SqlParser(sqlQuery);
        SelectQuery selectQuery = sqlParser.parseSelectQuery();
        return selectQuery;
    }

}
