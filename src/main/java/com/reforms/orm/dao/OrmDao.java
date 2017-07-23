package com.reforms.orm.dao;

import com.reforms.ann.ThreadSafe;
import com.reforms.orm.IConnectionHolder;
import com.reforms.orm.OrmConfigurator;
import com.reforms.orm.dao.batch.IBatcher;
import com.reforms.orm.dao.bobj.model.OrmHandler;
import com.reforms.orm.dao.bobj.model.OrmIterator;
import com.reforms.orm.dao.bobj.update.IInsertValues;
import com.reforms.orm.dao.bobj.update.IUpdateValues;
import com.reforms.orm.dao.column.SelectedColumn;
import com.reforms.orm.dao.filter.CallableValueSetter;
import com.reforms.orm.dao.filter.IPsValuesSetter;
import com.reforms.orm.extractor.OrmSelectColumnExtractorAndAliasModifier;
import com.reforms.orm.extractor.QueryPreparer;
import com.reforms.orm.extractor.SelectColumnCallableExtractor;
import com.reforms.sql.expr.query.*;
import com.reforms.sql.parser.SqlParser;

import java.sql.*;
import java.util.*;

import static com.reforms.orm.OrmConfigurator.getInstance;
import static com.reforms.orm.dao.column.SelectedColumnConst.SCC_SINGLE_COLUMN;

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
        SelectQuery selectQuery = parseSelectQuery(daoCtx.getQuery());
        OrmSelectColumnExtractorAndAliasModifier selectedColumnExtractor = OrmConfigurator.getInstance(OrmSelectColumnExtractorAndAliasModifier.class);
        List<SelectedColumn> selectedColumns = selectedColumnExtractor.extractSelectedColumns(selectQuery, daoCtx.getSelectedColumnFilter());
        IResultSetReaderFactory rsrFactory = getInstance(IResultSetReaderFactory.class);
        IResultSetObjectReader ormReader = rsrFactory.resolveReader(daoCtx.getOrmType(), selectedColumns);
        QueryPreparer filterPreparer = OrmConfigurator.getInstance(QueryPreparer.class);
        IPsValuesSetter paramSetterEngine = filterPreparer.prepareSelectQuery(selectQuery, daoCtx.getFilterValues());
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
        SelectQuery selectQuery = parseSelectQuery(daoCtx.getQuery());
        OrmSelectColumnExtractorAndAliasModifier selectedColumnExtractor = getInstance(OrmSelectColumnExtractorAndAliasModifier.class);
        List<SelectedColumn> selectedColumns = selectedColumnExtractor.extractSelectedColumns(selectQuery, daoCtx.getSelectedColumnFilter());
        IResultSetReaderFactory rsrFactory = getInstance(IResultSetReaderFactory.class);
        IResultSetObjectReader ormReader = rsrFactory.resolveReader(daoCtx.getOrmType(), selectedColumns);
        QueryPreparer filterPreparer = OrmConfigurator.getInstance(QueryPreparer.class);
        IPsValuesSetter paramSetterEngine = filterPreparer.prepareSelectQuery(selectQuery, daoCtx.getFilterValues());
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
    public <OrmType> Set<OrmType> set(DaoSelectContext daoCtx) throws Exception {
        IConnectionHolder cHolder = getInstance(IConnectionHolder.class);
        Connection connection = cHolder.getConnection(daoCtx.getConnectionHolder());
        SelectQuery selectQuery = parseSelectQuery(daoCtx.getQuery());
        OrmSelectColumnExtractorAndAliasModifier selectedColumnExtractor = getInstance(OrmSelectColumnExtractorAndAliasModifier.class);
        List<SelectedColumn> selectedColumns = selectedColumnExtractor.extractSelectedColumns(selectQuery, daoCtx.getSelectedColumnFilter());
        IResultSetReaderFactory rsrFactory = getInstance(IResultSetReaderFactory.class);
        IResultSetObjectReader ormReader = rsrFactory.resolveReader(daoCtx.getOrmType(), selectedColumns);
        QueryPreparer filterPreparer = OrmConfigurator.getInstance(QueryPreparer.class);
        IPsValuesSetter paramSetterEngine = filterPreparer.prepareSelectQuery(selectQuery, daoCtx.getFilterValues());
        String preparedSqlQuery = selectQuery.toString();
        Set<OrmType> orms = new HashSet<>();
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
        SelectQuery selectQuery = parseSelectQuery(daoCtx.getQuery());
        OrmSelectColumnExtractorAndAliasModifier selectedColumnExtractor = OrmConfigurator.getInstance(OrmSelectColumnExtractorAndAliasModifier.class);
        List<SelectedColumn> selectedColumns = selectedColumnExtractor.extractSelectedColumns(selectQuery, daoCtx.getSelectedColumnFilter());
        IResultSetReaderFactory rsrFactory = getInstance(IResultSetReaderFactory.class);
        IResultSetObjectReader ormReader = rsrFactory.resolveReader(daoCtx.getOrmType(), selectedColumns);
        QueryPreparer filterPreparer = OrmConfigurator.getInstance(QueryPreparer.class);
        IPsValuesSetter paramSetterEngine = filterPreparer.prepareSelectQuery(selectQuery, daoCtx.getFilterValues());
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
        SelectQuery selectQuery = parseSelectQuery(daoCtx.getQuery());
        OrmSelectColumnExtractorAndAliasModifier selectedColumnExtractor = OrmConfigurator.getInstance(OrmSelectColumnExtractorAndAliasModifier.class);
        List<SelectedColumn> selectedColumns = selectedColumnExtractor.extractSelectedColumns(selectQuery, daoCtx.getSelectedColumnFilter());
        IResultSetReaderFactory rsrFactory = getInstance(IResultSetReaderFactory.class);
        IResultSetObjectReader ormReader = rsrFactory.resolveReader(daoCtx.getOrmType(), selectedColumns);
        QueryPreparer filterPreparer = OrmConfigurator.getInstance(QueryPreparer.class);
        IPsValuesSetter paramSetterEngine = filterPreparer.prepareSelectQuery(selectQuery, daoCtx.getFilterValues());
        String preparedSqlQuery = selectQuery.toString();
        try (PreparedStatement ps = connection.prepareStatement(preparedSqlQuery)) {
            paramSetterEngine.setParamsTo(ps);
            try (ResultSet rs = ps.executeQuery()) {
                handler.startHandle();
                while (ormReader.canRead(rs)) {
                    Object orm = ormReader.read(rs);
                    if (!handler.handleOrm(orm)) {
                        break;
                    }
                }
                handler.endHandle();
            }
        }
    }

    @Override
    public int update(DaoUpdateContext daoCtx) throws Exception {
        IConnectionHolder cHolder = getInstance(IConnectionHolder.class);
        Connection connection = cHolder.getConnection(daoCtx.getConnectionHolder());
        UpdateQuery updateQuery = parseUpdateQuery(daoCtx.getQuery());
        QueryPreparer filterPreparer = OrmConfigurator.getInstance(QueryPreparer.class);
        IPsValuesSetter paramSetterEngine = filterPreparer.prepareUpdateQuery(updateQuery, daoCtx.getUpateValues(), daoCtx.getFilterValues());
        String preparedSqlQuery = updateQuery.toString();
        try (PreparedStatement ps = connection.prepareStatement(preparedSqlQuery)) {
            paramSetterEngine.setParamsTo(ps);
            return ps.executeUpdate();
        }
    }

    @Override
    public int[][] updates(DaoBatchUpdateContext daoCtx) throws Exception {
        IConnectionHolder cHolder = getInstance(IConnectionHolder.class);
        Connection connection = cHolder.getConnection(daoCtx.getConnectionHolder());
        UpdateQuery updateQuery = parseUpdateQuery(daoCtx.getQuery());
        QueryPreparer filterPreparer = OrmConfigurator.getInstance(QueryPreparer.class);
        Iterator<IUpdateValues> iterator = daoCtx.getUpateValues();
        if (!iterator.hasNext()) {
            throw new IllegalStateException("Не допускается указания пустого итератора по обновляемым данным");
        }
        IUpdateValues updateValues = iterator.next();
        IBatcher batcher = filterPreparer.prepareUpdateQueryWithBatch(updateQuery, updateValues);
        String preparedSqlQuery = updateQuery.toString();
        List<int[]> batchesResult = new ArrayList<>();
        boolean oldCommitState = connection.getAutoCommit();
        if (oldCommitState) {
            connection.setAutoCommit(false);
        }
        try (PreparedStatement ps = connection.prepareStatement(preparedSqlQuery)) {
            batcher.add(updateValues, ps);
            int currentBatchCount = 1;
            while (iterator.hasNext()) {
                updateValues = iterator.next();
                batcher.add(updateValues, ps);
                currentBatchCount++;
                if (currentBatchCount == daoCtx.getBatchSize()) {
                    currentBatchCount = 0;
                    int[] updateStateResults = ps.executeBatch();
                    batchesResult.add(updateStateResults);
                }
            }
            if (currentBatchCount < daoCtx.getBatchSize()) {
                currentBatchCount = 0;
                int[] updateStateResults = ps.executeBatch();
                batchesResult.add(updateStateResults);
            }
            if (oldCommitState) {
                connection.commit();
            }
        } catch (Exception cause) {
            if (oldCommitState) {
                connection.rollback();
            }
            throw cause;
        } finally {
            if (oldCommitState) {
                connection.setAutoCommit(true);
            }
        }
        return batchesResult.toArray(new int[batchesResult.size()][]);
    }

    @Override
    public int delete(DaoDeleteContext daoCtx) throws Exception {
        IConnectionHolder cHolder = getInstance(IConnectionHolder.class);
        Connection connection = cHolder.getConnection(daoCtx.getConnectionHolder());
        DeleteQuery updateQuery = parseDeleteQuery(daoCtx.getQuery());
        QueryPreparer filterPreparer = OrmConfigurator.getInstance(QueryPreparer.class);
        IPsValuesSetter paramSetterEngine = filterPreparer.prepareDeleteQuery(updateQuery, daoCtx.getFilterValues());
        String preparedSqlQuery = updateQuery.toString();
        try (PreparedStatement ps = connection.prepareStatement(preparedSqlQuery)) {
            paramSetterEngine.setParamsTo(ps);
            return ps.executeUpdate();
        }
    }

    @Override
    public Object insert(DaoInsertContext daoCtx) throws Exception {
        IConnectionHolder cHolder = getInstance(IConnectionHolder.class);
        Connection connection = cHolder.getConnection(daoCtx.getConnectionHolder());
        InsertQuery insertQuery = parseInsertQuery(daoCtx.getQuery());
        QueryPreparer filterPreparer = OrmConfigurator.getInstance(QueryPreparer.class);
        IPsValuesSetter paramSetterEngine = filterPreparer.prepareInsertQuery(insertQuery, daoCtx.getInsertValues());
        String preparedSqlQuery = insertQuery.toString();
        Class<?> keyClass = daoCtx.getKeyClass();
        boolean hasKey = keyClass != null;
        int updaterecordCount = 0;
        try (PreparedStatement ps = hasKey ?
                connection.prepareStatement(preparedSqlQuery, Statement.RETURN_GENERATED_KEYS) :
                    connection.prepareStatement(preparedSqlQuery)) {
            paramSetterEngine.setParamsTo(ps);
            updaterecordCount = ps.executeUpdate();
            if (hasKey && updaterecordCount > 0) {
                IResultSetReaderFactory rsrFactory = getInstance(IResultSetReaderFactory.class);
                IResultSetObjectReader rsReader = rsrFactory.resolveReader(keyClass, SCC_SINGLE_COLUMN);
                try (ResultSet rsKeys = ps.getGeneratedKeys()) {
                    if (rsReader.canRead(rsKeys)) {
                        return rsReader.read(rsKeys);
                    }
                }
                throw new IllegalStateException("Невозможно получить автогенерируемый ключ");
            }
        }
        return updaterecordCount;
    }

    @Override
    public int[][] inserts(DaoBatchInsertContext daoCtx) throws Exception {
        IConnectionHolder cHolder = getInstance(IConnectionHolder.class);
        Connection connection = cHolder.getConnection(daoCtx.getConnectionHolder());
        InsertQuery insertQuery = parseInsertQuery(daoCtx.getQuery());
        QueryPreparer filterPreparer = OrmConfigurator.getInstance(QueryPreparer.class);
        Iterator<IInsertValues> iterator = daoCtx.getInsertValues();
        if (!iterator.hasNext()) {
            throw new IllegalStateException("Не допускается указания пустого итератора по вставляемым данным");
        }
        IInsertValues insertValues = iterator.next();
        IBatcher batcher = filterPreparer.prepareInsertQueryWithBatch(insertQuery, insertValues);
        String preparedSqlQuery = insertQuery.toString();
        List<int[]> batchesResult = new ArrayList<>();
        boolean oldCommitState = connection.getAutoCommit();
        if (oldCommitState) {
            connection.setAutoCommit(false);
        }
        try (PreparedStatement ps = connection.prepareStatement(preparedSqlQuery)) {
            batcher.add(insertValues, ps);
            int currentBatchCount = 1;
            while (iterator.hasNext()) {
                insertValues = iterator.next();
                batcher.add(insertValues, ps);
                currentBatchCount++;
                if (currentBatchCount == daoCtx.getBatchSize()) {
                    currentBatchCount = 0;
                    int[] updateStateResults = ps.executeBatch();
                    batchesResult.add(updateStateResults);
                }
            }
            if (currentBatchCount < daoCtx.getBatchSize()) {
                currentBatchCount = 0;
                int[] updateStateResults = ps.executeBatch();
                batchesResult.add(updateStateResults);
            }
            if (oldCommitState) {
                connection.commit();
            }
        } catch (Exception cause) {
            if (oldCommitState) {
                connection.rollback();
            }
            throw cause;
        } finally {
            if (oldCommitState) {
                connection.setAutoCommit(true);
            }
        }
        return batchesResult.toArray(new int[batchesResult.size()][]);
    }

    @Override
    public <OrmType> OrmType callAndLoad(DaoCallContext daoCtx) throws Exception {
        IConnectionHolder cHolder = getInstance(IConnectionHolder.class);
        Connection connection = cHolder.getConnection(daoCtx.getConnectionHolder());
        CallQuery callQuery = parseCallQuery(daoCtx.getQuery());
        SelectColumnCallableExtractor selectedColumnExtractor = OrmConfigurator.getInstance(SelectColumnCallableExtractor.class);
        List<SelectedColumn> selectedColumns = selectedColumnExtractor.extractSelectedColumns(callQuery, daoCtx.getSelectedColumnFilter());
        IResultSetReaderFactory rsrFactory = getInstance(IResultSetReaderFactory.class);
        IResultSetObjectReader ormReader = rsrFactory.resolveReader(daoCtx.getOrmType(), selectedColumns);
        QueryPreparer filterPreparer = OrmConfigurator.getInstance(QueryPreparer.class);
        CallableValueSetter callSetterEngine = filterPreparer.prepareCallQuery(callQuery, daoCtx.getFilterValues());
        String preparedSqlQuery = callQuery.toString();
        try (CallableStatement cs = connection.prepareCall(preparedSqlQuery)) {
            callSetterEngine.setParamsAndReturnType(daoCtx.getReturnSqlType(), cs);
            try (ResultSet rs = cs.executeQuery()) {
                OrmType orm = null;
                if (ormReader.canRead(rs)) {
                    orm = ormReader.read(rs);
                }
                return orm;
            }
        }
    }

    @Override
    public <OrmType> List<OrmType> callAndLoads(DaoCallContext daoCtx) throws Exception {
        IConnectionHolder cHolder = getInstance(IConnectionHolder.class);
        Connection connection = cHolder.getConnection(daoCtx.getConnectionHolder());
        CallQuery callQuery = parseCallQuery(daoCtx.getQuery());
        SelectColumnCallableExtractor selectedColumnExtractor = OrmConfigurator.getInstance(SelectColumnCallableExtractor.class);
        List<SelectedColumn> selectedColumns = selectedColumnExtractor.extractSelectedColumns(callQuery, daoCtx.getSelectedColumnFilter());
        IResultSetReaderFactory rsrFactory = getInstance(IResultSetReaderFactory.class);
        IResultSetObjectReader ormReader = rsrFactory.resolveReader(daoCtx.getOrmType(), selectedColumns);
        QueryPreparer filterPreparer = OrmConfigurator.getInstance(QueryPreparer.class);
        CallableValueSetter callSetterEngine = filterPreparer.prepareCallQuery(callQuery, daoCtx.getFilterValues());
        String preparedSqlQuery = callQuery.toString();
        List<OrmType> orms = new ArrayList<>();
        try (CallableStatement cs = connection.prepareCall(preparedSqlQuery)) {
            callSetterEngine.setParamsAndReturnType(daoCtx.getReturnSqlType(), cs);
            try (ResultSet rs = cs.executeQuery()) {
                while (ormReader.canRead(rs)) {
                    OrmType orm = ormReader.read(rs);
                    orms.add(orm);
                }
                return orms;
            }
        }
    }

    @Override
    public <OrmType> OrmIterator<OrmType> callAndIterate(DaoCallContext daoCtx) throws Exception {
        IConnectionHolder cHolder = getInstance(IConnectionHolder.class);
        Connection connection = cHolder.getConnection(daoCtx.getConnectionHolder());
        CallQuery callQuery = parseCallQuery(daoCtx.getQuery());
        SelectColumnCallableExtractor selectedColumnExtractor = OrmConfigurator.getInstance(SelectColumnCallableExtractor.class);
        List<SelectedColumn> selectedColumns = selectedColumnExtractor.extractSelectedColumns(callQuery, daoCtx.getSelectedColumnFilter());
        IResultSetReaderFactory rsrFactory = getInstance(IResultSetReaderFactory.class);
        IResultSetObjectReader ormReader = rsrFactory.resolveReader(daoCtx.getOrmType(), selectedColumns);
        QueryPreparer filterPreparer = OrmConfigurator.getInstance(QueryPreparer.class);
        CallableValueSetter callSetterEngine = filterPreparer.prepareCallQuery(callQuery, daoCtx.getFilterValues());
        String preparedSqlQuery = callQuery.toString();
        CallableStatement cs = null;
        try {
            cs = connection.prepareCall(preparedSqlQuery);
            callSetterEngine.setParamsAndReturnType(daoCtx.getReturnSqlType(), cs);
            OrmIterator<OrmType> ormIterator = new OrmIterator<OrmType>(cs, ormReader);
            ormIterator.prepare();
            return ormIterator;
        } catch (Exception ex) {
            if (cs != null) {
                cs.close();
            }
            throw ex;
        }
    }

    @Override
    public void callAndHandle(DaoCallContext daoCtx, OrmHandler<Object> handler) throws Exception {
        IConnectionHolder cHolder = getInstance(IConnectionHolder.class);
        Connection connection = cHolder.getConnection(daoCtx.getConnectionHolder());
        CallQuery callQuery = parseCallQuery(daoCtx.getQuery());
        SelectColumnCallableExtractor selectedColumnExtractor = OrmConfigurator.getInstance(SelectColumnCallableExtractor.class);
        List<SelectedColumn> selectedColumns = selectedColumnExtractor.extractSelectedColumns(callQuery, daoCtx.getSelectedColumnFilter());
        IResultSetReaderFactory rsrFactory = getInstance(IResultSetReaderFactory.class);
        IResultSetObjectReader ormReader = rsrFactory.resolveReader(daoCtx.getOrmType(), selectedColumns);
        QueryPreparer filterPreparer = OrmConfigurator.getInstance(QueryPreparer.class);
        CallableValueSetter callSetterEngine = filterPreparer.prepareCallQuery(callQuery, daoCtx.getFilterValues());
        String preparedSqlQuery = callQuery.toString();
        try (CallableStatement cs = connection.prepareCall(preparedSqlQuery)) {
            callSetterEngine.setParamsAndReturnType(daoCtx.getReturnSqlType(), cs);
            try (ResultSet rs = cs.executeQuery()) {
                handler.startHandle();
                while (ormReader.canRead(rs)) {
                    Object orm = ormReader.read(rs);
                    if (!handler.handleOrm(orm)) {
                        break;
                    }
                }
                handler.endHandle();
            }
        }
    }

    private SelectQuery parseSelectQuery(String sqlQuery) {
        SqlParser sqlParser = new SqlParser(sqlQuery);
        SelectQuery selectQuery = sqlParser.parseSelectQuery();
        return selectQuery;
    }

    private UpdateQuery parseUpdateQuery(String sqlQuery) {
        SqlParser sqlParser = new SqlParser(sqlQuery);
        UpdateQuery updateQuery = sqlParser.parseUpdateQuery();
        return updateQuery;
    }

    private DeleteQuery parseDeleteQuery(String sqlQuery) {
        SqlParser sqlParser = new SqlParser(sqlQuery);
        DeleteQuery updateQuery = sqlParser.parseDeleteQuery();
        return updateQuery;
    }

    private InsertQuery parseInsertQuery(String sqlQuery) {
        SqlParser sqlParser = new SqlParser(sqlQuery);
        InsertQuery insertQuery = sqlParser.parseInsertQuery();
        return insertQuery;
    }

    private CallQuery parseCallQuery(String sqlQuery) {
        SqlParser sqlParser = new SqlParser(sqlQuery);
        CallQuery callQuery = sqlParser.parseCallQuery();
        return callQuery;
    }
}
