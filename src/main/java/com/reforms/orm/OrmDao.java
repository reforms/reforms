package com.reforms.orm;

import static com.reforms.orm.filter.FilterMap.EMPTY_FILTER_MAP;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.reforms.orm.extractor.SelectColumnExtractorAndAliasModifier;
import com.reforms.orm.filter.*;
import com.reforms.orm.reflex.Reflexor;
import com.reforms.orm.select.SelectedColumn;
import com.reforms.orm.select.bobj.ResultSetOrmReader;
import com.reforms.sql.expr.query.SelectQuery;
import com.reforms.sql.parser.SqlParser;

public class OrmDao {

    private Object connectionHolder;

    public OrmDao(Object connectionHolder) {
        this.connectionHolder = connectionHolder;
    }

    public <OrmType> OrmType loadOrm(Class<OrmType> ormClass, String sqlQuery, Object filterBobj) throws Exception {
        return loadOrm(ormClass, sqlQuery, new FilterObject(filterBobj));
    }

    public <OrmType> OrmType loadSimpleOrm(Class<OrmType> ormClass, String sqlQuery, Object... filters) throws Exception {
        return loadOrm(ormClass, sqlQuery, new FilterSequence(filters));
    }

    @SuppressWarnings("unchecked")
    public <OrmType> OrmType loadOrm(Class<OrmType> ormClass, String sqlQuery, FilterValues filters) throws Exception {
        OrmContext rCtx = OrmConfigurator.get(OrmContext.class);
        IConnectionHolder cHolder = rCtx.getConnectionHolder();
        Connection connection = cHolder.getConnection(connectionHolder);
        SelectQuery selectQuery = parseSqlQuery(sqlQuery);
        SelectColumnExtractorAndAliasModifier selectedColumnExtractor = new SelectColumnExtractorAndAliasModifier();
        List<SelectedColumn> selectedColumns = selectedColumnExtractor.extractSelectedColumns(selectQuery);
        ResultSetOrmReader rsReader = new ResultSetOrmReader(selectedColumns, Reflexor.createReflexor(ormClass), rCtx);
        SelectQueryFilterPreparer filterPreparer = new SelectQueryFilterPreparer();
        FilterPrepareStatementSetter paramSetterEngine = filterPreparer.prepareFilters(selectQuery, filters);
        String preparedSqlQuery = selectQuery.toString();
        try (PreparedStatement ps = connection.prepareStatement(preparedSqlQuery)) {
            paramSetterEngine.setParamsTo(ps);
            try (ResultSet rs = ps.executeQuery()) {
                Object orm = rsReader.read(rs);
                return (OrmType) orm;
            }
        }
    }

    public <OrmType> List<OrmType> loadOrms(Class<OrmType> ormClass, String sqlQuery) throws Exception {
        return loadOrms(ormClass, sqlQuery, EMPTY_FILTER_MAP);
    }

    public <OrmType> List<OrmType> loadOrms(Class<OrmType> ormClass, String sqlQuery, Object filterBobj) throws Exception {
        return loadOrms(ormClass, sqlQuery, new FilterObject(filterBobj));
    }

    public <OrmType> List<OrmType> loadSimpleOrms(Class<OrmType> ormClass, String sqlQuery, Object... filters) throws Exception {
        return loadOrms(ormClass, sqlQuery, new FilterSequence(filters));
    }

    @SuppressWarnings("unchecked")
    public <OrmType> List<OrmType> loadOrms(Class<OrmType> ormClass, String sqlQuery, FilterValues filters) throws Exception {
        OrmContext rCtx = OrmConfigurator.get(OrmContext.class);
        IConnectionHolder cHolder = rCtx.getConnectionHolder();
        Connection connection = cHolder.getConnection(connectionHolder);
        SelectQuery selectQuery = parseSqlQuery(sqlQuery);
        SelectColumnExtractorAndAliasModifier selectedColumnExtractor = new SelectColumnExtractorAndAliasModifier();
        List<SelectedColumn> selectedColumns = selectedColumnExtractor.extractSelectedColumns(selectQuery);
        ResultSetOrmReader rsReader = new ResultSetOrmReader(selectedColumns, Reflexor.createReflexor(ormClass), rCtx);
        SelectQueryFilterPreparer filterPreparer = new SelectQueryFilterPreparer();
        FilterPrepareStatementSetter paramSetterEngine = filterPreparer.prepareFilters(selectQuery, filters);
        String preparedSqlQuery = selectQuery.toString();
        List<OrmType> orms = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(preparedSqlQuery)) {
            paramSetterEngine.setParamsTo(ps);
            try (ResultSet rs = ps.executeQuery()) {
                Object orm = null;
                while ((orm = rsReader.read(rs)) != null) {
                    orms.add((OrmType) orm);
                }
            }
        }
        return orms;
    }

    private SelectQuery parseSqlQuery(String sqlQuery) {
        SqlParser sqlParser = new SqlParser(sqlQuery);
        SelectQuery selectQuery = sqlParser.parseSelectQuery();
        return selectQuery;
    }

}
