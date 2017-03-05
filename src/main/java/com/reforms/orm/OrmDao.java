package com.reforms.orm;

import static com.reforms.orm.OrmConfigurator.getInstance;
import static com.reforms.orm.filter.FilterMap.EMPTY_FILTER_MAP;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.reforms.orm.extractor.OrmSelectColumnExtractorAndAliasModifier;
import com.reforms.orm.filter.*;
import com.reforms.orm.select.SelectedColumn;
import com.reforms.orm.select.bobj.ResultSetOrmReader;
import com.reforms.orm.select.bobj.model.OrmHandler;
import com.reforms.orm.select.bobj.model.OrmIterator;
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
        IConnectionHolder cHolder = getInstance(IConnectionHolder.class);
        Connection connection = cHolder.getConnection(connectionHolder);
        SelectQuery selectQuery = parseSqlQuery(sqlQuery);
        OrmSelectColumnExtractorAndAliasModifier selectedColumnExtractor = OrmConfigurator.getInstance(OrmSelectColumnExtractorAndAliasModifier.class);
        List<SelectedColumn> selectedColumns = selectedColumnExtractor.extractSelectedColumns(selectQuery);
        ResultSetOrmReader rsReader = new ResultSetOrmReader(ormClass, selectedColumns);
        SelectQueryPreparer filterPreparer = OrmConfigurator.getInstance(SelectQueryPreparer.class);
        FilterPrepareStatementSetter paramSetterEngine = filterPreparer.prepare(selectQuery, filters);
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
        IConnectionHolder cHolder = getInstance(IConnectionHolder.class);
        Connection connection = cHolder.getConnection(connectionHolder);
        SelectQuery selectQuery = parseSqlQuery(sqlQuery);
        OrmSelectColumnExtractorAndAliasModifier selectedColumnExtractor = OrmConfigurator.getInstance(OrmSelectColumnExtractorAndAliasModifier.class);
        List<SelectedColumn> selectedColumns = selectedColumnExtractor.extractSelectedColumns(selectQuery);
        ResultSetOrmReader rsReader = new ResultSetOrmReader(ormClass, selectedColumns);
        SelectQueryPreparer filterPreparer = OrmConfigurator.getInstance(SelectQueryPreparer.class);
        FilterPrepareStatementSetter paramSetterEngine = filterPreparer.prepare(selectQuery, filters);
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

    public <OrmType> void handleOrms(Class<OrmType> ormClass, String sqlQuery, OrmHandler<OrmType> handler, Object filterBobj)
            throws Exception {
        handleOrms(ormClass, sqlQuery, handler, new FilterObject(filterBobj));
    }

    public <OrmType> void handleOrms(Class<OrmType> ormClass, String sqlQuery, OrmHandler<OrmType> handler) throws Exception {
        handleOrms(ormClass, sqlQuery, handler, EMPTY_FILTER_MAP);
    }

    public <OrmType> void handleSimpleOrms(Class<OrmType> ormClass, String sqlQuery, OrmHandler<OrmType> handler, Object... filters)
            throws Exception {
        handleOrms(ormClass, sqlQuery, handler, new FilterSequence(filters));
    }

    public <OrmType> void handleOrms(Class<OrmType> ormClass, String sqlQuery, OrmHandler<OrmType> handler, FilterValues filters)
            throws Exception {
        IConnectionHolder cHolder = getInstance(IConnectionHolder.class);
        Connection connection = cHolder.getConnection(connectionHolder);
        SelectQuery selectQuery = parseSqlQuery(sqlQuery);
        OrmSelectColumnExtractorAndAliasModifier selectedColumnExtractor = OrmConfigurator.getInstance(OrmSelectColumnExtractorAndAliasModifier.class);
        List<SelectedColumn> selectedColumns = selectedColumnExtractor.extractSelectedColumns(selectQuery);
        ResultSetOrmReader rsReader = new ResultSetOrmReader(ormClass, selectedColumns);
        SelectQueryPreparer filterPreparer = OrmConfigurator.getInstance(SelectQueryPreparer.class);
        FilterPrepareStatementSetter paramSetterEngine = filterPreparer.prepare(selectQuery, filters);
        String preparedSqlQuery = selectQuery.toString();
        try (PreparedStatement ps = connection.prepareStatement(preparedSqlQuery)) {
            paramSetterEngine.setParamsTo(ps);
            try (ResultSet rs = ps.executeQuery()) {
                handler.startHandle();
                Object orm = null;
                while ((orm = rsReader.read(rs)) != null) {
                    handler.handleOrm((OrmType) orm);
                }
                handler.endHandle();
            }
        }
    }

    public <OrmType> OrmIterator<OrmType> loadOrmIterator(Class<OrmType> ormClass, String sqlQuery, Object filterBobj) throws Exception {
        return loadOrmIterator(ormClass, sqlQuery, new FilterObject(filterBobj));
    }

    public <OrmType> OrmIterator<OrmType> loadOrmIterator(Class<OrmType> ormClass, String sqlQuery) throws Exception {
        return loadOrmIterator(ormClass, sqlQuery, EMPTY_FILTER_MAP);
    }

    public <OrmType> OrmIterator<OrmType> loadSimpleOrmIterator(Class<OrmType> ormClass, String sqlQuery, Object... filters)
            throws Exception {
        return loadOrmIterator(ormClass, sqlQuery, new FilterSequence(filters));
    }

    public <OrmType> OrmIterator<OrmType> loadOrmIterator(Class<OrmType> ormClass, String sqlQuery, FilterValues filters) throws Exception {
        IConnectionHolder cHolder = getInstance(IConnectionHolder.class);
        Connection connection = cHolder.getConnection(connectionHolder);
        SelectQuery selectQuery = parseSqlQuery(sqlQuery);
        OrmSelectColumnExtractorAndAliasModifier selectedColumnExtractor = OrmConfigurator.getInstance(OrmSelectColumnExtractorAndAliasModifier.class);
        List<SelectedColumn> selectedColumns = selectedColumnExtractor.extractSelectedColumns(selectQuery);
        ResultSetOrmReader rsReader = new ResultSetOrmReader(ormClass, selectedColumns);
        SelectQueryPreparer filterPreparer = OrmConfigurator.getInstance(SelectQueryPreparer.class);
        FilterPrepareStatementSetter paramSetterEngine = filterPreparer.prepare(selectQuery, filters);
        String preparedSqlQuery = selectQuery.toString();
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(preparedSqlQuery);
            paramSetterEngine.setParamsTo(ps);
            OrmIterator<OrmType> ormIterator = new OrmIterator<OrmType>(ps, rsReader);
            ormIterator.prepare();
            return ormIterator;
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
