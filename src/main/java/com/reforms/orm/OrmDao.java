package com.reforms.orm;

import static com.reforms.orm.dao.filter.FilterMap.EMPTY_FILTER_MAP;
import static com.reforms.orm.dao.filter.column.AllSelectedColumnFilter.ALL_COLUMNS_FILTER;

import java.util.List;
import java.util.Map;

import com.reforms.orm.dao.OrmDaoAdapter;
import com.reforms.orm.dao.bobj.IOrmDaoAdapter;
import com.reforms.orm.dao.bobj.model.OrmHandler;
import com.reforms.orm.dao.bobj.model.OrmIterator;
import com.reforms.orm.dao.bobj.update.*;
import com.reforms.orm.dao.filter.FilterMap;
import com.reforms.orm.dao.filter.FilterObject;
import com.reforms.orm.dao.filter.FilterSequence;
import com.reforms.orm.dao.filter.IFilterValues;
import com.reforms.orm.dao.filter.column.ISelectedColumnFilter;

/**
 * Фасадные методы для доступа к БД
 * @author evgenie
 */
public class OrmDao {

    /** Container for java.sql.Connection */
    private Object connectionHolder;

    /**
     * Constructor with your container for java.sql.Connection.<br>
     * <code>connectionHolder</code> - it's one of classes:<br>
     *  1. java.sql.Connection<br>
     *  2. javax.sql.DataSource<br>
     *  3. any Class with 'java.sql.Connection getConnection()' method<br>
     * @param connectionHolder - your container for java.sql.Connection;
     */
    public OrmDao(Object connectionHolder) {
        this.connectionHolder = connectionHolder;
    }

    /**
     * Select single object with <code>sqlQuery</code> by <code>filters</code>.<br>
     * Example of usage:<pre><code>
     * // Your Orm class
     * public class Client {
     *     private long clientId;
     *     private String clientName;
     *     // get/set method ...
     * }
     * // Your Dao class
     * public class ClientDao {
     *
     *     // Reform api - dao
     *     private OrmDao ormDao;
     *
     *     public ClientDao(Connection connection) {
     *         ormDao = new OrmDao(connection);
     *     }
     *     // SQL SELECT QUERY to find client
     *     private static final String FIND_CLIENT_QUERY = "SELECT client_id, client_name FROM clients WHERE client_id = ?";
     *
     *     public Client findClient(long clientId) {
     *         return ormDao.select(Client.class, FIND_CLIENT_QUERY, clientId);
     *     }
     * }
     * </code></pre>
     *
     * @param ormClass - type of object. Supported types:<br>
     *        Boolean.class, boolean.class, Byte.class, byte.class, Short.class, short.class, Integer.class, int.class,
     *        Float.class, float.class, Double.class, double.class, Long.class, long.class, Enum.class, String.class,
     *        BigInteger.class, BigDecimal.class, java.sql.Date.class, java.sql.Timestamp.class, java.sql.Time.class, java.util.Date.class
     *        byte[].class, <i>YourOrm.class</i><br>
     *
     * @param sqlQuery - sql select query. For example: '<code>SELECT client_id, client_name FROM clients WHERE client_id = ?</code>';
     * @param filters - filter values for select query. In the example above clientId in <code>findClient</code> method is filter value
     * @return single object of ormClass type
     * @throws Exception any exception, SQLException, ReflectiveOperationException and other
     */
    public <OrmType> OrmType select(Class<OrmType> ormClass, String sqlQuery, Object ... filters) throws Exception {
        return selectOrm(ormClass, sqlQuery, ALL_COLUMNS_FILTER, new FilterSequence(filters));
    }

    /**
     * Update records with <code>sqlQuery</code> by <code>values</code>.
     * Example of usage:<pre><code>
     * // Your Dao class
     * public class ClientDao {
     *
     *     // Reform api - dao
     *     private OrmDao ormDao;
     *
     *     public ClientDao(Connection connection) {
     *         ormDao = new OrmDao(connection);
     *     }
     *     // SQL UPDATE QUERY update client name
     *     private static final String UPDATE_CLIENT_NAME_QUERY = "UPDATE clients SET client_name = ? WHERE client_id = ?";
     *
     *     public int updateClientName(long clientId, String clientName) {
     *         return ormDao.update(UPDATE_CLIENT_NAME_QUERY, clientName, clientId);
     *     }
     * }
     * </code></pre>
     * @param sqlQuery - sql update query. For example: '<code>UPDATE clients SET client_name = ? WHERE client_id = ?</code>';
     * @param values   - values to update AND filter records. In the example above in <code>updateClientName</code> method clientName is update source, clientId is filter value
     * @return count of updating records
     * @throws Exception any exception, SQLException, ReflectiveOperationException and other
     */
    public int update(String sqlQuery, Object ... values) throws Exception {
        return updateOrm(sqlQuery, new UpdateSequence(values), EMPTY_FILTER_MAP);
    }

    /**
     * Delete records with <code>sqlQuery</code> by <code>filters</code>.
     * Example of usage:<pre><code>
     * // Your Dao class
     * public class ClientDao {
     *
     *     // Reform api - dao
     *     private OrmDao ormDao;
     *
     *     public ClientDao(Connection connection) {
     *         ormDao = new OrmDao(connection);
     *     }
     *     // SQL DELETE QUERY delete client by id
     *     private static final String DELETE_CLIENT_QUERY = "DELETE FROM clients WHERE client_id = ?";
     *
     *     public int deleteClient(long clientId) {
     *         return ormDao.delete(DELETE_CLIENT_QUERY, clientId);
     *     }
     * }
     * </code></pre>
     * @param sqlQuery - sql delete query. For example: '<code>DELETE FROM clients WHERE client_id = ?</code>';
     * @param filters  - filter values for delete query.
     * @return count of deleting records
     * @throws Exception any exception, SQLException, ReflectiveOperationException and other
     */
    public int delete(String sqlQuery, Object ... filters) throws Exception {
        return deleteOrm(sqlQuery, new FilterSequence(filters));
    }

    /**
     * Insert record with <code>sqlQuery</code> by <code>values</code>.
     * Example of usage:<pre><code>
     * // Your Dao class
     * public class ClientDao {
     *
     *     // Reform api - dao
     *     private OrmDao ormDao;
     *
     *     public ClientDao(Connection connection) {
     *         ormDao = new OrmDao(connection);
     *     }
     *     // SQL INSERT QUERY insert client
     *     private static final String INSERT_CLIENT_QUERY = "INSERT INTO clients (client_id, client_name) VALUES(?, ?)";
     *
     *     public void insertClient(long clientId, String clientName) {
     *         return ormDao.insert(INSERT_CLIENT_QUERY, clientId, clientName);
     *     }
     * }
     * </code></pre>
     * @param sqlQuery - sql insert query. For example: '<code>INSERT INTO clients (client_id, client_name) VALUES(?, ?)</code>';
     * @param filters  - filter values for insert query.
     * @return count of deleting records
     * @throws Exception any exception, SQLException, ReflectiveOperationException and other
     */
    public void insert(String sqlQuery, Object ... values) throws Exception {
        insertOrm(sqlQuery, new UpdateSequence(values));
    }


    public <OrmType> OrmType selectOrm(Class<OrmType> ormClass, String sqlQuery, Object filterBobj) throws Exception {
        return selectOrm(ormClass, sqlQuery, ALL_COLUMNS_FILTER, new FilterObject(filterBobj));
    }

    public <OrmType> OrmType selectOrm(Class<OrmType> ormClass, String sqlQuery, Map<String, Object> filterMap) throws Exception {
        return selectOrm(ormClass, sqlQuery, ALL_COLUMNS_FILTER, new FilterMap(filterMap));
    }

    public <OrmType> OrmType selectOrm(Class<OrmType> ormClass, String sqlQuery, ISelectedColumnFilter solumnFilter, Object filterBobj)
            throws Exception {
        return selectOrm(ormClass, sqlQuery, solumnFilter, new FilterObject(filterBobj));
    }

    public <OrmType> OrmType selectOrm(Class<OrmType> ormClass, String sqlQuery, ISelectedColumnFilter solumnFilter, Map<String, Object> filterMap)
            throws Exception {
        return selectOrm(ormClass, sqlQuery, solumnFilter, new FilterMap(filterMap));
    }

    public <OrmType> OrmType selectOrm(Class<OrmType> ormClass, String sqlQuery, IFilterValues filters) throws Exception {
        return selectOrm(ormClass, sqlQuery, ALL_COLUMNS_FILTER, filters);
    }

    public <OrmType> OrmType selectSimpleOrm(Class<OrmType> ormClass, String sqlQuery, Object... filters) throws Exception {
        return selectOrm(ormClass, sqlQuery, ALL_COLUMNS_FILTER, new FilterSequence(filters));
    }

    public <OrmType> OrmType selectSimpleOrm(Class<OrmType> ormClass, String sqlQuery, ISelectedColumnFilter solumnFilter, Object... filters)
            throws Exception {
        return selectOrm(ormClass, sqlQuery, solumnFilter, new FilterSequence(filters));
    }

    public <OrmType> OrmType selectOrm(Class<OrmType> ormClass, String sqlQuery, ISelectedColumnFilter solumnFilter, IFilterValues filters)
            throws Exception {
        IOrmDaoAdapter daoAdapter = createDao(connectionHolder, sqlQuery);
        daoAdapter.setSelectedColumnFilter(solumnFilter);
        daoAdapter.setFilterValue(filters);
        return daoAdapter.load(ormClass);
    }

    public <OrmType> List<OrmType> selectOrms(Class<OrmType> ormClass, String sqlQuery) throws Exception {
        return selectOrms(ormClass, sqlQuery, ALL_COLUMNS_FILTER, EMPTY_FILTER_MAP);
    }

    public <OrmType> List<OrmType> selectOrms(Class<OrmType> ormClass, String sqlQuery, ISelectedColumnFilter solumnFilter) throws Exception {
        return selectOrms(ormClass, sqlQuery, solumnFilter, EMPTY_FILTER_MAP);
    }

    public <OrmType> List<OrmType> selectOrms(Class<OrmType> ormClass, String sqlQuery, Object filterBobj) throws Exception {
        return selectOrms(ormClass, sqlQuery, ALL_COLUMNS_FILTER, new FilterObject(filterBobj));
    }

    public <OrmType> List<OrmType> selectOrms(Class<OrmType> ormClass, String sqlQuery, Map<String, Object> filterMap) throws Exception {
        return selectOrms(ormClass, sqlQuery, ALL_COLUMNS_FILTER, new FilterMap(filterMap));
    }

    public <OrmType> List<OrmType> selectOrms(Class<OrmType> ormClass, String sqlQuery, IFilterValues filter) throws Exception {
        return selectOrms(ormClass, sqlQuery, ALL_COLUMNS_FILTER, filter);
    }

    public <OrmType> List<OrmType> selectOrms(Class<OrmType> ormClass, String sqlQuery, ISelectedColumnFilter solumnFilter, Object filterBobj)
            throws Exception {
        return selectOrms(ormClass, sqlQuery, solumnFilter, new FilterObject(filterBobj));
    }

    public <OrmType> List<OrmType> selectOrms(Class<OrmType> ormClass, String sqlQuery, ISelectedColumnFilter solumnFilter, Map<String, Object> filterMap)
            throws Exception {
        return selectOrms(ormClass, sqlQuery, solumnFilter, new FilterMap(filterMap));
    }

    public <OrmType> List<OrmType> selectSimpleOrms(Class<OrmType> ormClass, String sqlQuery, Object... filters) throws Exception {
        return selectOrms(ormClass, sqlQuery, ALL_COLUMNS_FILTER, new FilterSequence(filters));
    }

    public <OrmType> List<OrmType> selectSimpleOrms(Class<OrmType> ormClass, String sqlQuery, ISelectedColumnFilter solumnFilter,
            Object... filters) throws Exception {
        return selectOrms(ormClass, sqlQuery, solumnFilter, new FilterSequence(filters));
    }

    public <OrmType> List<OrmType> selectOrms(Class<OrmType> ormClass, String sqlQuery, ISelectedColumnFilter solumnFilter,
            IFilterValues filters) throws Exception {
        IOrmDaoAdapter daoAdapter = createDao(connectionHolder, sqlQuery);
        daoAdapter.setSelectedColumnFilter(solumnFilter);
        daoAdapter.setFilterValue(filters);
        return daoAdapter.loads(ormClass);
    }

    public <OrmType> void handleSelectedOrms(Class<OrmType> ormClass, String sqlQuery, OrmHandler<OrmType> handler, Object filterBobj)
            throws Exception {
        handleSelectedOrms(ormClass, sqlQuery, handler, ALL_COLUMNS_FILTER, new FilterObject(filterBobj));
    }

    public <OrmType> void handleSelectedOrms(Class<OrmType> ormClass, String sqlQuery, OrmHandler<OrmType> handler, Map<String, Object> filterMap)
            throws Exception {
        handleSelectedOrms(ormClass, sqlQuery, handler, ALL_COLUMNS_FILTER, new FilterMap(filterMap));
    }

    public <OrmType> void handleSelectedOrms(Class<OrmType> ormClass, String sqlQuery, OrmHandler<OrmType> handler, IFilterValues filter)
            throws Exception {
        handleSelectedOrms(ormClass, sqlQuery, handler, ALL_COLUMNS_FILTER, filter);
    }

    public <OrmType> void handleSelectedOrms(Class<OrmType> ormClass, String sqlQuery, OrmHandler<OrmType> handler,
            ISelectedColumnFilter solumnFilter, Object filterBobj)
            throws Exception {
        handleSelectedOrms(ormClass, sqlQuery, handler, solumnFilter, new FilterObject(filterBobj));
    }

    public <OrmType> void handleSelectedOrms(Class<OrmType> ormClass, String sqlQuery, OrmHandler<OrmType> handler,
            ISelectedColumnFilter solumnFilter, Map<String, Object> filterMap)
            throws Exception {
        handleSelectedOrms(ormClass, sqlQuery, handler, solumnFilter, new FilterMap(filterMap));
    }

    public <OrmType> void handleSelectedOrms(Class<OrmType> ormClass, String sqlQuery, OrmHandler<OrmType> handler) throws Exception {
        handleSelectedOrms(ormClass, sqlQuery, handler, ALL_COLUMNS_FILTER, EMPTY_FILTER_MAP);
    }

    public <OrmType> void handleSelectedOrms(Class<OrmType> ormClass, String sqlQuery, OrmHandler<OrmType> handler,
            ISelectedColumnFilter solumnFilter) throws Exception {
        handleSelectedOrms(ormClass, sqlQuery, handler, solumnFilter, EMPTY_FILTER_MAP);
    }

    public <OrmType> void handleSelectedSimpleOrms(Class<OrmType> ormClass, String sqlQuery, OrmHandler<OrmType> handler, Object... filters)
            throws Exception {
        handleSelectedOrms(ormClass, sqlQuery, handler, ALL_COLUMNS_FILTER, new FilterSequence(filters));
    }

    public <OrmType> void handleSelectedSimpleOrms(Class<OrmType> ormClass, String sqlQuery, OrmHandler<OrmType> handler,
            ISelectedColumnFilter solumnFilter, Object... filters)
            throws Exception {
        handleSelectedOrms(ormClass, sqlQuery, handler, solumnFilter, new FilterSequence(filters));
    }

    public <OrmType> void handleSelectedOrms(Class<OrmType> ormClass, String sqlQuery, OrmHandler<OrmType> handler,
            ISelectedColumnFilter solumnFilter, IFilterValues filters)
            throws Exception {
        IOrmDaoAdapter daoAdapter = createDao(connectionHolder, sqlQuery);
        daoAdapter.setSelectedColumnFilter(solumnFilter);
        daoAdapter.setFilterValue(filters);
        daoAdapter.handle(ormClass, handler);
    }

    public <OrmType> OrmIterator<OrmType> selectOrmIterator(Class<OrmType> ormClass, String sqlQuery, Object filterBobj) throws Exception {
        return selectOrmIterator(ormClass, sqlQuery, ALL_COLUMNS_FILTER, new FilterObject(filterBobj));
    }

    public <OrmType> OrmIterator<OrmType> selectOrmIterator(Class<OrmType> ormClass, String sqlQuery, Map<String, Object> filterMap) throws Exception {
        return selectOrmIterator(ormClass, sqlQuery, ALL_COLUMNS_FILTER, new FilterMap(filterMap));
    }

    public <OrmType> OrmIterator<OrmType> selectOrmIterator(Class<OrmType> ormClass, String sqlQuery, IFilterValues filter) throws Exception {
        return selectOrmIterator(ormClass, sqlQuery, ALL_COLUMNS_FILTER, filter);
    }

    public <OrmType> OrmIterator<OrmType> selectOrmIterator(Class<OrmType> ormClass, String sqlQuery, ISelectedColumnFilter solumnFilter,
            Object filterBobj) throws Exception {
        return selectOrmIterator(ormClass, sqlQuery, solumnFilter, new FilterObject(filterBobj));
    }

    public <OrmType> OrmIterator<OrmType> selectOrmIterator(Class<OrmType> ormClass, String sqlQuery, ISelectedColumnFilter solumnFilter,
            Map<String, Object> filterMap) throws Exception {
        return selectOrmIterator(ormClass, sqlQuery, solumnFilter, new FilterMap(filterMap));
    }

    public <OrmType> OrmIterator<OrmType> selectOrmIterator(Class<OrmType> ormClass, String sqlQuery) throws Exception {
        return selectOrmIterator(ormClass, sqlQuery, ALL_COLUMNS_FILTER, EMPTY_FILTER_MAP);
    }

    public <OrmType> OrmIterator<OrmType> selectOrmIterator(Class<OrmType> ormClass, String sqlQuery, ISelectedColumnFilter solumnFilter)
            throws Exception {
        return selectOrmIterator(ormClass, sqlQuery, solumnFilter, EMPTY_FILTER_MAP);
    }

    public <OrmType> OrmIterator<OrmType> selectSimpleOrmIterator(Class<OrmType> ormClass, String sqlQuery, Object... filters)
            throws Exception {
        return selectOrmIterator(ormClass, sqlQuery, ALL_COLUMNS_FILTER, new FilterSequence(filters));
    }

    public <OrmType> OrmIterator<OrmType> selectSimpleOrmIterator(Class<OrmType> ormClass, String sqlQuery,
            ISelectedColumnFilter solumnFilter, Object... filters)
            throws Exception {
        return selectOrmIterator(ormClass, sqlQuery, solumnFilter, new FilterSequence(filters));
    }

    public <OrmType> OrmIterator<OrmType> selectOrmIterator(Class<OrmType> ormClass, String sqlQuery, ISelectedColumnFilter solumnFilter,
            IFilterValues filters) throws Exception {
        IOrmDaoAdapter daoAdapter = createDao(connectionHolder, sqlQuery);
        daoAdapter.setSelectedColumnFilter(solumnFilter);
        daoAdapter.setFilterValue(filters);
        return daoAdapter.iterate(ormClass);
    }

    public int updateOrm(String sqlQuery, Object updateBobj) throws Exception {
        return updateOrm(sqlQuery, new UpdateObject(updateBobj), EMPTY_FILTER_MAP);
    }

    public int updateOrm(String sqlQuery, IUpdateValues updateValues) throws Exception {
        return updateOrm(sqlQuery, updateValues, EMPTY_FILTER_MAP);
    }

    public int updateOrm(String sqlQuery, Map<String, Object> updateMap) throws Exception {
        return updateOrm(sqlQuery, new UpdateMap(updateMap), EMPTY_FILTER_MAP);
    }

    public int updateOrm(String sqlQuery, Object updateBobj, Object filterBobj) throws Exception {
        return updateOrm(sqlQuery, new UpdateObject(updateBobj), new FilterObject(filterBobj));
    }

    public int updateOrm(String sqlQuery, Object updateBobj, Map<String, Object> filterMap) throws Exception {
        return updateOrm(sqlQuery, new UpdateObject(updateBobj), new FilterMap(filterMap));
    }

    public int updateOrm(String sqlQuery, Map<String, Object> updateMap, Map<String, Object> filterMap) throws Exception {
        return updateOrm(sqlQuery, new UpdateMap(updateMap), new FilterMap(filterMap));
    }

    public int updateSimpleOrm(String sqlQuery, Object ... values) throws Exception {
        return updateOrm(sqlQuery, new UpdateSequence(values), EMPTY_FILTER_MAP);
    }

    public int updateOrm(String sqlQuery, IUpdateValues updateValues, IFilterValues filters) throws Exception {
        IOrmDaoAdapter daoAdapter = createDao(connectionHolder, sqlQuery);
        daoAdapter.setUpdateValue(updateValues);
        daoAdapter.setFilterValue(filters);
        return daoAdapter.update();
    }

    public int deleteOrm(String sqlQuery, IFilterValues filters) throws Exception {
        IOrmDaoAdapter daoAdapter = createDao(connectionHolder, sqlQuery);
        daoAdapter.setFilterValue(filters);
        return daoAdapter.delete();
    }

    public void insertOrm(String sqlQuery, IUpdateValues values) throws Exception {
        IOrmDaoAdapter daoAdapter = createDao(connectionHolder, sqlQuery);
        daoAdapter.setInsertValue(values);
        daoAdapter.insert();
    }

    public static IOrmDaoAdapter createDao(Object connectionHolder, String sqlQuery) {
        return new OrmDaoAdapter(connectionHolder, sqlQuery);
    }
}