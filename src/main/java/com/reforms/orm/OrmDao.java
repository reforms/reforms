package com.reforms.orm;

import com.reforms.orm.dao.OrmDaoAdapter;
import com.reforms.orm.dao.bobj.IOrmDaoAdapter;
import com.reforms.orm.dao.bobj.model.OrmHandler;
import com.reforms.orm.dao.bobj.model.OrmIterator;
import com.reforms.orm.dao.bobj.update.IUpdateValues;
import com.reforms.orm.dao.bobj.update.UpdateMap;
import com.reforms.orm.dao.bobj.update.UpdateObject;
import com.reforms.orm.dao.bobj.update.UpdateSequence;
import com.reforms.orm.dao.filter.FilterMap;
import com.reforms.orm.dao.filter.FilterObject;
import com.reforms.orm.dao.filter.FilterSequence;
import com.reforms.orm.dao.filter.IFilterValues;
import com.reforms.orm.dao.filter.column.ISelectedColumnFilter;
import com.reforms.orm.dao.proxy.DaoProxy;
import com.reforms.orm.dao.proxy.IMethodInterceptor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;

import static com.reforms.orm.OrmConfigurator.getInstance;
import static com.reforms.orm.dao.filter.FilterMap.EMPTY_FILTER_MAP;
import static com.reforms.orm.dao.filter.column.DefaultSelectedColumnFilter.DEFAULT_COLUMNS_FILTER;

/**
 * Фасадные методы для доступа к БД<br>
 * Usage example N1: Clear SQL and filter query with simple values:<pre><code>
 *
 * public <b>class Client</b> {
 *
 *     private long id;
 *
 *     private String name;
 *
 *     private ClientState state;
 *
 *     public long getId() {
 *         return id;
 *     }
 *
 *     public void setId(long id) {
 *         this.id = id;
 *     }
 *
 *     public String getName() {
 *         return name;
 *     }
 *
 *     public void setName(String name) {
 *         this.name = name;
 *     }
 *
 *     public ClientState getState() {
 *         return state;
 *     }
 *
 *     public void setState(ClientState state) {
 *         this.state = state;
 *     }
 * }
 *
 * public <b>enum ClientState</b> {
 *     NEW(0),
 *     ACTIVE(1),
 *     BLOCKED(2);
 *
 *     {@literal @}TargetField
 *     private int state;
 *
 *     private ClientState(int state) {
 *         this.state = state;
 *     }
 *
 *     public int getState() {
 *         return state;
 *     }
 *
 *     {@literal @}TargetField
 *     public static ClientState getClientState(int state) {
 *        for (ClientState clientState : values()) {
 *             if (clientState.state == state) {
 *                 return clientState;
 *             }
 *         }
 *         throw new IllegalStateException("Unknown client with state " + state);
 *     }
 * }
 *
 * public <b>class ClientDao</b> {
 *
 *     // Reform api - dao
 *     private OrmDao ormDao;

 *     public ClientDao(Connection connection) {
 *         ormDao = new OrmDao(connection);
 *     }
 *
 *     // SQL SELECT QUERY to load all active clients
 *     private static final String SELECT_ACTIVE_CLIENTS_QUERY = "SELECT id, name, state FROM clients WHERE state = ?";
 *
 *     public List<Client> loadActiveClients() throws Exception {
 *         return ormDao.<b>selectList</b>(Client.class, SELECT_ACTIVE_CLIENTS_QUERY, ClientState.ACTIVE);
 *     }
 *
 *     // SQL SELECT QUERY to find client
 *     private static final String FIND_CLIENT_QUERY = "SELECT id, name, state FROM clients WHERE id = ?";
 *
 *     public Client findClient(long clientId) throws Exception {
 *         return ormDao.<b>select</b>(Client.class, FIND_CLIENT_QUERY, clientId);
 *     }
 *
 *     // SQL UPDATE QUERY update client name
 *     private static final String UPDATE_CLIENT_QUERY = "UPDATE clients SET name = ?, state = ? WHERE id = ?";
 *
 *     public int updateClientName(long clientId, String clientName, ClientState clientState) throws Exception {
 *         return ormDao.<b>update</b>(UPDATE_CLIENT_QUERY, clientName, clientState, clientId);
 *     }
 *
 *     // SQL DELETE QUERY delete client by id
 *     private static final String DELETE_CLIENT_QUERY = "DELETE FROM clients WHERE id = ?";
 *
 *     public int deleteClient(long clientId) throws Exception {
 *         return ormDao.<b>delete</b>(DELETE_CLIENT_QUERY, clientId);
 *     }
 *
 *     // SQL INSERT QUERY insert client
 *     private static final String INSERT_CLIENT_QUERY = "INSERT INTO clients (id, name, state) VALUES(?, ?, ?)";
 *
 *     public void saveClient(long clientId, String clientName, ClientState clientState) throws Exception {
 *         ormDao.<b>insert</b>(INSERT_CLIENT_QUERY, clientId, clientName, clientState);
 *     }
 * }
 * </code></pre>
 * @author evgenie
 */
public class OrmDao {

    /** Container for java.sql.Connection */
    private final Object connectionHolder;

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
     * // Your ORM
     * public class Client {
     *
     *     private long id;
     *     private String name;
     *     private ClientState state;
     *
     *     public long getId() {
     *         return id;
     *     }
     *     public void setId(long id) {
     *         this.id = id;
     *     }
     *
     *     public String getName() {
     *         return name;
     *     }
     *     public void setName(String name) {
     *         this.name = name;
     *     }
     *
     *     public ClientState getState() {
     *         return state;
     *     }
     *     public void setState(ClientState state) {
     *         this.state = state;
     *     }
     * }
     *
     * // Your ENUM of client states
     * public enum ClientState {
     *     NEW(0),
     *     ACTIVE(1),
     *     BLOCKED(2);
     *
     *     {@literal @}TargetField
     *     private final int state;
     *
     *     private ClientState(int state) {
     *         this.state = state;
     *     }
     *
     *     public int getState() {
     *         return state;
     *     }
     *
     *     {@literal @}TargetMethod
     *     public static ClientState getClientState(int state) {
     *         for (ClientState clientState : values()) {
     *             if (clientState.state == state) {
     *                 return clientState;
     *             }
     *         }
     *         throw new IllegalStateException("Unknown client with state " + state);
     *     }
     * }
     * // Your DAO
     * public class ClientDao {
     *
     *     // Reform api - dao
     *     private OrmDao ormDao;
     *
     *     public ClientDao(Connection connection) {
     *         ormDao = new OrmDao(connection);
     *     }
     *
     *     // SQL SELECT QUERY to find client
     *     private static final String FIND_CLIENT_QUERY = "SELECT id, name, state FROM clients WHERE id = ?";
     *
     *     public Client findClient(long clientId) throws Exception {
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
     * @param sqlQuery - sql select query. For example: '<code>SELECT id, name, state FROM clients WHERE id = ?</code>';
     * @param filters - filter values for select query. In the example above clientId in <code>findClient</code> method is filter value
     * @return single object of ormClass type
     * @throws Exception any exception, SQLException, ReflectiveOperationException and other
     */
    public <OrmType> OrmType select(Class<OrmType> ormClass, String sqlQuery, Object ... filters) throws Exception {
        return selectOrm(ormClass, sqlQuery, DEFAULT_COLUMNS_FILTER, new FilterSequence(filters));
    }

    /**
     * Select list of objects with <code>sqlQuery</code> by <code>filters</code>.<br>
     * Example of usage:<pre><code>
     * // Your ORM
     * public class Client {
     *
     *     private long id;
     *     private String name;
     *     private ClientState state;
     *
     *     public long getId() {
     *         return id;
     *     }
     *     public void setId(long id) {
     *         this.id = id;
     *     }
     *
     *     public String getName() {
     *         return name;
     *     }
     *     public void setName(String name) {
     *         this.name = name;
     *     }
     *
     *     public ClientState getState() {
     *         return state;
     *     }
     *     public void setState(ClientState state) {
     *         this.state = state;
     *     }
     * }
     *
     * // Your ENUM of client states
     * public enum ClientState {
     *     NEW(0),
     *     ACTIVE(1),
     *     BLOCKED(2);
     *
     *     {@literal @}TargetField
     *     private final int state;
     *
     *     private ClientState(int state) {
     *         this.state = state;
     *     }
     *
     *     public int getState() {
     *         return state;
     *     }
     *
     *     {@literal @}TargetMethod
     *     public static ClientState getClientState(int state) {
     *         for (ClientState clientState : values()) {
     *             if (clientState.state == state) {
     *                 return clientState;
     *             }
     *         }
     *         throw new IllegalStateException("Unknown client with state " + state);
     *     }
     * }
     * // Your DAO
     * public class ClientDao {
     *
     *     // Reform api - dao
     *     private OrmDao ormDao;
     *
     *     public ClientDao(Connection connection) {
     *         ormDao = new OrmDao(connection);
     *     }
     *
     *     // SQL SELECT QUERY to load all active clients
     *     private static final String SELECT_ACTIVE_CLIENTS_QUERY = "SELECT id, name, state FROM clients WHERE state = ?";
     *
     *     public List<Client> loadActiveClients() throws Exception {
     *         return ormDao.selectList(Client.class, SELECT_ACTIVE_CLIENTS_QUERY, ClientState.ACTIVE);
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
     * @param sqlQuery - sql select query. For example: '<code>SELECT id, name, state FROM clients WHERE state = ?</code>';
     * @param filters - filter values for select query. In the example above clientId in <code>findClient</code> method is filter value
     * @return single object of ormClass type
     * @throws Exception any exception, SQLException, ReflectiveOperationException and other
     */
    public <OrmType> List<OrmType> selectList(Class<OrmType> ormClass, String sqlQuery, Object ... filters) throws Exception {
        return selectOrms(ormClass, sqlQuery, DEFAULT_COLUMNS_FILTER, new FilterSequence(filters));
    }

    /**
     * Select object iterator with <code>sqlQuery</code> by <code>filters</code>.<br>
     * Example of usage:<pre><code>
     * // Your ORM
     * public class Client {
     *
     *     private long id;
     *     private String name;
     *     private ClientState state;
     *
     *     public long getId() {
     *         return id;
     *     }
     *     public void setId(long id) {
     *         this.id = id;
     *     }
     *
     *     public String getName() {
     *         return name;
     *     }
     *     public void setName(String name) {
     *         this.name = name;
     *     }
     *
     *     public ClientState getState() {
     *         return state;
     *     }
     *     public void setState(ClientState state) {
     *         this.state = state;
     *     }
     * }
     *
     * // Your ENUM of client states
     * public enum ClientState {
     *     NEW(0),
     *     ACTIVE(1),
     *     BLOCKED(2);
     *
     *     {@literal @}TargetField
     *     private final int state;
     *
     *     private ClientState(int state) {
     *         this.state = state;
     *     }
     *
     *     public int getState() {
     *         return state;
     *     }
     *
     *     {@literal @}TargetMethod
     *     public static ClientState getClientState(int state) {
     *         for (ClientState clientState : values()) {
     *             if (clientState.state == state) {
     *                 return clientState;
     *             }
     *         }
     *         throw new IllegalStateException("Unknown client with state " + state);
     *     }
     * }
     * // Your DAO
     * public class ClientDao {
     *
     *     // Reform api - dao
     *     private OrmDao ormDao;
     *
     *     public ClientDao(Connection connection) {
     *         ormDao = new OrmDao(connection);
     *     }
     *
     *    // SQL SELECT QUERY to load all clients
     *    private static final String SELECT_ALL_CLIENTS_QUERY = "SELECT id, name, state FROM clients";
     *
     *    public OrmIterator<Client> loadClients() throws Exception {
     *        return ormDao.selectIterator(Client.class, SELECT_ALL_CLIENTS_QUERY);
     *    }
     * }
     * </code></pre>
     *
     * @param ormClass - type of object. Supported types:<br>
     *        Boolean.class, boolean.class, Byte.class, byte.class, Short.class, short.class, Integer.class, int.class,
     *        Float.class, float.class, Double.class, double.class, Long.class, long.class, Enum.class, String.class,
     *        BigInteger.class, BigDecimal.class, java.sql.Date.class, java.sql.Timestamp.class, java.sql.Time.class, java.util.Date.class
     *        byte[].class, <i>YourOrm.class</i><br>
     *
     * @param sqlQuery - sql select query. For example: '<code>SELECT id, name, state FROM clients WHERE state = ?</code>';
     * @param filters - filter values for select query. In the example above clientId in <code>findClient</code> method is filter value
     * @return single object of ormClass type
     * @throws Exception any exception, SQLException, ReflectiveOperationException and other
     */
    public <OrmType> OrmIterator<OrmType> selectIterator(Class<OrmType> ormClass, String sqlQuery, Object ... filters) throws Exception {
        return selectOrmIterator(ormClass, sqlQuery, DEFAULT_COLUMNS_FILTER, new FilterSequence(filters));
    }

    /**
     * Select object iterator with <code>sqlQuery</code> by <code>filters</code>.<br>
     * Example of usage:<pre><code>
     * // Your ORM
     * public class Client {
     *
     *     private long id;
     *     private String name;
     *     private ClientState state;
     *
     *     public long getId() {
     *         return id;
     *     }
     *     public void setId(long id) {
     *         this.id = id;
     *     }
     *
     *     public String getName() {
     *         return name;
     *     }
     *     public void setName(String name) {
     *         this.name = name;
     *     }
     *
     *     public ClientState getState() {
     *         return state;
     *     }
     *     public void setState(ClientState state) {
     *         this.state = state;
     *     }
     * }
     *
     * // Your ENUM of client states
     * public enum ClientState {
     *     NEW(0),
     *     ACTIVE(1),
     *     BLOCKED(2);
     *
     *     {@literal @}TargetField
     *     private final int state;
     *
     *     private ClientState(int state) {
     *         this.state = state;
     *     }
     *
     *     public int getState() {
     *         return state;
     *     }
     *
     *     {@literal @}TargetMethod
     *     public static ClientState getClientState(int state) {
     *         for (ClientState clientState : values()) {
     *             if (clientState.state == state) {
     *                 return clientState;
     *             }
     *         }
     *         throw new IllegalStateException("Unknown client with state " + state);
     *     }
     * }
     * // Your handler of client
     * public class ClientHandler implements OrmHandler<Client> {
     *
     *     private int index;
     *
     *     {@literal @}Override
     *     public void startHandle() {
     *         index = 0;
     *         System.out.println("beging...");
     *     }

     *     {@literal @}Override
     *     public boolean handleOrm(Client dbClient) {
     *         index++;
     *         System.out.println("Load client: " + dbClient);
     *         return true;
     *     }
     *
     *     {@literal @}Override
     *     public void endHandle() {
     *         System.out.println("end... Total: " + index);
     *     }
     * }
     * // Your DAO
     * public class ClientDao {
     *
     *     // Reform api - dao
     *     private OrmDao ormDao;
     *
     *     public ClientDao(Connection connection) {
     *         ormDao = new OrmDao(connection);
     *     }
     *
     *    // SQL SELECT QUERY to load all clients
     *    private static final String SELECT_ALL_CLIENTS_QUERY = "SELECT id, name, state FROM clients";
     *
     *    public void processClients(ClientHandler clientHandler) throws Exception {
     *        ormDao.selectAndHandle(Client.class, SELECT_ALL_CLIENTS_QUERY, clientHandler);
     *    }
     *
     * }
     * </code></pre>
     *
     * @param ormClass - type of object. Supported types:<br>
     *        Boolean.class, boolean.class, Byte.class, byte.class, Short.class, short.class, Integer.class, int.class,
     *        Float.class, float.class, Double.class, double.class, Long.class, long.class, Enum.class, String.class,
     *        BigInteger.class, BigDecimal.class, java.sql.Date.class, java.sql.Timestamp.class, java.sql.Time.class, java.util.Date.class
     *        byte[].class, <i>YourOrm.class</i><br>
     *
     * @param sqlQuery - sql select query. For example: '<code>SELECT id, name, state FROM clients WHERE state = ?</code>';
     * @param filters - filter values for select query. In the example above clientId in <code>findClient</code> method is filter value
     * @throws Exception any exception, SQLException, ReflectiveOperationException and other
     */
    public <OrmType> void selectAndHandle(Class<OrmType> ormClass, String sqlQuery, OrmHandler<OrmType> handler, Object ... filters) throws Exception {
        handleSelectedOrms(ormClass, sqlQuery, handler, DEFAULT_COLUMNS_FILTER, new FilterSequence(filters));
    }

    /**
     * Update records with <code>sqlQuery</code> by <code>values</code>.
     * Example of usage:<pre><code>
     * // Your ORM
     * public class ClientDao {
     *
     *     // Reform api - dao
     *     private OrmDao ormDao;
     *
     *     public ClientDao(Connection connection) {
     *         ormDao = new OrmDao(connection);
     *     }
     *     // SQL UPDATE QUERY update client name
     *     private static final String UPDATE_CLIENT_QUERY = "UPDATE clients SET name = ?, state = ? WHERE id = ?";
     *
     *     public int updateClientName(long clientId, String clientName, ClientState clientState) throws Exception {
     *         return ormDao.update(UPDATE_CLIENT_QUERY, clientName, clientState, clientId);
     *     }
     * </code></pre>
     * @param sqlQuery - sql update query. For example: '<code>UPDATE clients SET name = ?, state = ? WHERE id = ?</code>';
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
     * // Your ORM
     * public class ClientDao {
     *
     *     // Reform api - dao
     *     private OrmDao ormDao;
     *
     *     public ClientDao(Connection connection) {
     *         ormDao = new OrmDao(connection);
     *     }
     *     // SQL DELETE QUERY delete client by id
     *     private static final String DELETE_CLIENT_QUERY = "DELETE FROM clients WHERE id = ?";
     *
     *     public int deleteClient(long clientId) throws Exception {
     *         return ormDao.delete(DELETE_CLIENT_QUERY, clientId);
     *     }
     * }
     * </code></pre>
     * @param sqlQuery - sql delete query. For example: '<code>DELETE FROM clients WHERE id = ?</code>';
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
     * // Your ORM
     * public class ClientDao {
     *
     *     // Reform api - dao
     *     private OrmDao ormDao;
     *
     *     public ClientDao(Connection connection) {
     *         ormDao = new OrmDao(connection);
     *     }
     *     // SQL INSERT QUERY insert client
     *     private static final String INSERT_CLIENT_QUERY = "INSERT INTO clients (id, name, state) VALUES(?, ?, ?)";
     *
     *     public void saveClient(long clientId, String clientName, ClientState clientState) throws Exception {
     *         ormDao.insert(INSERT_CLIENT_QUERY, clientId, clientName, clientState);
     *     }
     * }
     * </code></pre>
     * @param sqlQuery - sql insert query. For example: '<code>INSERT INTO clients (client_id, client_name) VALUES(?, ?)</code>';
     * @param values значения
     * @throws Exception any exception, SQLException, ReflectiveOperationException and other
     */
    public void insert(String sqlQuery, Object ... values) throws Exception {
        insertOrm(sqlQuery, new UpdateSequence(values));
    }


    public <OrmType> OrmType selectOrm(Class<OrmType> ormClass, String sqlQuery, Object filterBobj) throws Exception {
        return selectOrm(ormClass, sqlQuery, DEFAULT_COLUMNS_FILTER, new FilterObject(filterBobj));
    }

    public <OrmType> OrmType selectOrm(Class<OrmType> ormClass, String sqlQuery, Map<String, Object> filterMap) throws Exception {
        return selectOrm(ormClass, sqlQuery, DEFAULT_COLUMNS_FILTER, new FilterMap(filterMap));
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
        return selectOrm(ormClass, sqlQuery, DEFAULT_COLUMNS_FILTER, filters);
    }

    public <OrmType> OrmType selectSimpleOrm(Class<OrmType> ormClass, String sqlQuery, Object... filters) throws Exception {
        return selectOrm(ormClass, sqlQuery, DEFAULT_COLUMNS_FILTER, new FilterSequence(filters));
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
        return selectOrms(ormClass, sqlQuery, DEFAULT_COLUMNS_FILTER, EMPTY_FILTER_MAP);
    }

    public <OrmType> List<OrmType> selectOrms(Class<OrmType> ormClass, String sqlQuery, ISelectedColumnFilter solumnFilter) throws Exception {
        return selectOrms(ormClass, sqlQuery, solumnFilter, EMPTY_FILTER_MAP);
    }

    public <OrmType> List<OrmType> selectOrms(Class<OrmType> ormClass, String sqlQuery, Object filterBobj) throws Exception {
        return selectOrms(ormClass, sqlQuery, DEFAULT_COLUMNS_FILTER, new FilterObject(filterBobj));
    }

    public <OrmType> List<OrmType> selectOrms(Class<OrmType> ormClass, String sqlQuery, Map<String, Object> filterMap) throws Exception {
        return selectOrms(ormClass, sqlQuery, DEFAULT_COLUMNS_FILTER, new FilterMap(filterMap));
    }

    public <OrmType> List<OrmType> selectOrms(Class<OrmType> ormClass, String sqlQuery, IFilterValues filter) throws Exception {
        return selectOrms(ormClass, sqlQuery, DEFAULT_COLUMNS_FILTER, filter);
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
        return selectOrms(ormClass, sqlQuery, DEFAULT_COLUMNS_FILTER, new FilterSequence(filters));
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
        handleSelectedOrms(ormClass, sqlQuery, handler, DEFAULT_COLUMNS_FILTER, new FilterObject(filterBobj));
    }

    public <OrmType> void handleSelectedOrms(Class<OrmType> ormClass, String sqlQuery, OrmHandler<OrmType> handler, Map<String, Object> filterMap)
            throws Exception {
        handleSelectedOrms(ormClass, sqlQuery, handler, DEFAULT_COLUMNS_FILTER, new FilterMap(filterMap));
    }

    public <OrmType> void handleSelectedOrms(Class<OrmType> ormClass, String sqlQuery, OrmHandler<OrmType> handler, IFilterValues filter)
            throws Exception {
        handleSelectedOrms(ormClass, sqlQuery, handler, DEFAULT_COLUMNS_FILTER, filter);
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
        handleSelectedOrms(ormClass, sqlQuery, handler, DEFAULT_COLUMNS_FILTER, EMPTY_FILTER_MAP);
    }

    public <OrmType> void handleSelectedOrms(Class<OrmType> ormClass, String sqlQuery, OrmHandler<OrmType> handler,
            ISelectedColumnFilter solumnFilter) throws Exception {
        handleSelectedOrms(ormClass, sqlQuery, handler, solumnFilter, EMPTY_FILTER_MAP);
    }

    public <OrmType> void handleSelectedSimpleOrms(Class<OrmType> ormClass, String sqlQuery, OrmHandler<OrmType> handler, Object... filters)
            throws Exception {
        handleSelectedOrms(ormClass, sqlQuery, handler, DEFAULT_COLUMNS_FILTER, new FilterSequence(filters));
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
        return selectOrmIterator(ormClass, sqlQuery, DEFAULT_COLUMNS_FILTER, new FilterObject(filterBobj));
    }

    public <OrmType> OrmIterator<OrmType> selectOrmIterator(Class<OrmType> ormClass, String sqlQuery, Map<String, Object> filterMap) throws Exception {
        return selectOrmIterator(ormClass, sqlQuery, DEFAULT_COLUMNS_FILTER, new FilterMap(filterMap));
    }

    public <OrmType> OrmIterator<OrmType> selectOrmIterator(Class<OrmType> ormClass, String sqlQuery, IFilterValues filter) throws Exception {
        return selectOrmIterator(ormClass, sqlQuery, DEFAULT_COLUMNS_FILTER, filter);
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
        return selectOrmIterator(ormClass, sqlQuery, DEFAULT_COLUMNS_FILTER, EMPTY_FILTER_MAP);
    }

    public <OrmType> OrmIterator<OrmType> selectOrmIterator(Class<OrmType> ormClass, String sqlQuery, ISelectedColumnFilter solumnFilter)
            throws Exception {
        return selectOrmIterator(ormClass, sqlQuery, solumnFilter, EMPTY_FILTER_MAP);
    }

    public <OrmType> OrmIterator<OrmType> selectSimpleOrmIterator(Class<OrmType> ormClass, String sqlQuery, Object... filters)
            throws Exception {
        return selectOrmIterator(ormClass, sqlQuery, DEFAULT_COLUMNS_FILTER, new FilterSequence(filters));
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

    @SuppressWarnings("unchecked")
    public static <Interfaze> Interfaze createDao(Object connectionHolder, Class<Interfaze> daoInterface) {
        ClassLoader classLoader = daoInterface.getClassLoader();
        Class<?>[] daoClasses = new Class[]{daoInterface};
        IMethodInterceptor intercepter = getInstance(IMethodInterceptor.class);
        InvocationHandler handler = new DaoProxy(connectionHolder, daoInterface, intercepter);
        return (Interfaze) Proxy.newProxyInstance(classLoader, daoClasses, handler);
    }
}