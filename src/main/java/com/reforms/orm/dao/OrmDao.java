package com.reforms.orm.dao;

import com.reforms.ann.ThreadSafe;
import com.reforms.orm.IConnectionHolder;
import com.reforms.orm.OrmConfigurator;
import com.reforms.orm.dao.bobj.model.OrmHandler;
import com.reforms.orm.dao.bobj.model.OrmIterator;
import com.reforms.orm.dao.column.SelectedColumn;
import com.reforms.orm.dao.filter.PrepareStatementValuesSetter;
import com.reforms.orm.extractor.OrmSelectColumnExtractorAndAliasModifier;
import com.reforms.orm.extractor.QueryPreparer;
import com.reforms.sql.expr.query.SelectQuery;
import com.reforms.sql.parser.SqlParser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static com.reforms.orm.OrmConfigurator.getInstance;

/**
 *
 * @author evgenie
 */
@ThreadSafe
class OrmDao implements IOrmDao {

    @Override
    public <OrmType> OrmType load(DaoSelectContext daoCtx) throws Exception {
        IConnectionHolder cHolder = getInstance(IConnectionHolder.class);
        Connection connection = cHolder.getConnection(daoCtx.getConnectionHolder());
        SelectQuery selectQuery = parseSqlQuery(daoCtx.getQuery());
        OrmSelectColumnExtractorAndAliasModifier selectedColumnExtractor = OrmConfigurator.getInstance(OrmSelectColumnExtractorAndAliasModifier.class);
        List<SelectedColumn> selectedColumns = selectedColumnExtractor.extractSelectedColumns(selectQuery, daoCtx.getSelectedColumnFilter());
        IResultSetReaderFactory rsrFactory = getInstance(IResultSetReaderFactory.class);
        IResultSetObjectReader ormReader = rsrFactory.resolveReader(daoCtx.getOrmType(), selectedColumns);
        QueryPreparer filterPreparer = OrmConfigurator.getInstance(QueryPreparer.class);
        PrepareStatementValuesSetter paramSetterEngine = filterPreparer.prepareSelectQuery(selectQuery, daoCtx.getFilterValues());
        String preparedSqlQuery = selectQuery.toString();
        try (PreparedStatement ps = connection.prepareStatement(preparedSqlQuery)) {
            paramSetterEngine.setParamsTo(ps);
            try (ResultSet rs = ps.executeQuery()) {
                OrmType orm = null;
                if (ormReader.canRead(rs)) {
                    orm = ormReader.read(rs);
                }
                return orm;
            }
        }
    }

    @Override
    public <OrmType> List<OrmType> loads(DaoSelectContext daoCtx) throws Exception {
        IConnectionHolder cHolder = getInstance(IConnectionHolder.class);
        Connection connection = cHolder.getConnection(daoCtx.getConnectionHolder());
        SelectQuery selectQuery = parseSqlQuery(daoCtx.getQuery());
        OrmSelectColumnExtractorAndAliasModifier selectedColumnExtractor = getInstance(OrmSelectColumnExtractorAndAliasModifier.class);
        List<SelectedColumn> selectedColumns = selectedColumnExtractor.extractSelectedColumns(selectQuery, daoCtx.getSelectedColumnFilter());
        IResultSetReaderFactory rsrFactory = getInstance(IResultSetReaderFactory.class);
        IResultSetObjectReader ormReader = rsrFactory.resolveReader(daoCtx.getOrmType(), selectedColumns);
        QueryPreparer filterPreparer = OrmConfigurator.getInstance(QueryPreparer.class);
        PrepareStatementValuesSetter paramSetterEngine = filterPreparer.prepareSelectQuery(selectQuery, daoCtx.getFilterValues());
        String preparedSqlQuery = selectQuery.toString();
        List<OrmType> orms = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(preparedSqlQuery)) {
            paramSetterEngine.setParamsTo(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (ormReader.canRead(rs)) {
                    OrmType orm = ormReader.read(rs);
                    orms.add(orm);
                }
            }
        }
        return orms;
    }

    @Override
    public <OrmType> OrmIterator<OrmType> iterate(DaoSelectContext daoCtx) throws Exception {
        IConnectionHolder cHolder = getInstance(IConnectionHolder.class);
        Connection connection = cHolder.getConnection(daoCtx.getConnectionHolder());
        SelectQuery selectQuery = parseSqlQuery(daoCtx.getQuery());
        OrmSelectColumnExtractorAndAliasModifier selectedColumnExtractor = OrmConfigurator.getInstance(OrmSelectColumnExtractorAndAliasModifier.class);
        List<SelectedColumn> selectedColumns = selectedColumnExtractor.extractSelectedColumns(selectQuery, daoCtx.getSelectedColumnFilter());
        IResultSetReaderFactory rsrFactory = getInstance(IResultSetReaderFactory.class);
        IResultSetObjectReader ormReader = rsrFactory.resolveReader(daoCtx.getOrmType(), selectedColumns);
        QueryPreparer filterPreparer = OrmConfigurator.getInstance(QueryPreparer.class);
        PrepareStatementValuesSetter paramSetterEngine = filterPreparer.prepareSelectQuery(selectQuery, daoCtx.getFilterValues());
        String preparedSqlQuery = selectQuery.toString();
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(preparedSqlQuery);
            paramSetterEngine.setParamsTo(ps);
            OrmIterator<OrmType> ormIterator = new OrmIterator<OrmType>(ps, ormReader);
            ormIterator.prepare();
            return ormIterator;
        } catch (Exception ex) {
            if (ps != null) {
                ps.close();
            }
            throw ex;
        }
    }

    @Override
    public void handle(DaoSelectContext daoCtx, OrmHandler<Object> handler) throws Exception {
        IConnectionHolder cHolder = getInstance(IConnectionHolder.class);
        Connection connection = cHolder.getConnection(daoCtx.getConnectionHolder());
        SelectQuery selectQuery = parseSqlQuery(daoCtx.getQuery());
        OrmSelectColumnExtractorAndAliasModifier selectedColumnExtractor = OrmConfigurator.getInstance(OrmSelectColumnExtractorAndAliasModifier.class);
        List<SelectedColumn> selectedColumns = selectedColumnExtractor.extractSelectedColumns(selectQuery, daoCtx.getSelectedColumnFilter());
        IResultSetReaderFactory rsrFactory = getInstance(IResultSetReaderFactory.class);
        IResultSetObjectReader ormReader = rsrFactory.resolveReader(daoCtx.getOrmType(), selectedColumns);
        QueryPreparer filterPreparer = OrmConfigurator.getInstance(QueryPreparer.class);
        PrepareStatementValuesSetter paramSetterEngine = filterPreparer.prepareSelectQuery(selectQuery, daoCtx.getFilterValues());
        String preparedSqlQuery = selectQuery.toString();
        try (PreparedStatement ps = connection.prepareStatement(preparedSqlQuery)) {
            paramSetterEngine.setParamsTo(ps);
            try (ResultSet rs = ps.executeQuery()) {
                handler.startHandle();
                while (ormReader.canRead(rs)) {
                    Object orm = ormReader.read(rs);
                    handler.handleOrm(orm);
                }
                handler.endHandle();
            }
        }
    }

    @Override
    public int update(DaoUpdateContext daoCtx) throws Exception {
        return 0;
    }

    private SelectQuery parseSqlQuery(String sqlQuery) {
        SqlParser sqlParser = new SqlParser(sqlQuery);
        SelectQuery selectQuery = sqlParser.parseSelectQuery();
        return selectQuery;
    }

}
