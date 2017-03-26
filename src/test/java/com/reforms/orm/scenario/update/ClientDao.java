package com.reforms.orm.scenario.update;

import com.reforms.orm.H2DataSource;
import com.reforms.orm.OrmDao;
import com.reforms.orm.dao.bobj.update.IUpdateValues;
import com.reforms.orm.dao.bobj.update.UpdateMap;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Слой доступа к данным по
 * @author evgenie
 */
class ClientDao {

    private OrmDao ormDao;

    public ClientDao(H2DataSource h2ds) {
        ormDao = new OrmDao(h2ds);
    }

    private static final String LOAD_CLIENT_QUERY =
            "SELECT id, name, act_time t#, version " +
            "    FROM client " +
            "        WHERE id = ? " +
            "           ORDER BY id ASC";

    public ClientOrm loadClient(long id) throws Exception {
        return ormDao.selectSimpleOrm(ClientOrm.class, LOAD_CLIENT_QUERY, id);
    }

    private static final String UPDATE_CLIENT_QUERY =
            "UPDATE client " +
            "   SET name = :name, act_time = :t#act_time, version = :version " +
            "       WHERE id = :id";

    public int updateFullClient(ClientOrm clientInfo) throws Exception {
        return ormDao.updateOrm(UPDATE_CLIENT_QUERY, clientInfo);
    }

    private static final String UPDATE_CLIENT_NAME_QUERY =
            "UPDATE client " +
                    "   SET name = :name " +
                    "       WHERE id = :id";

    public int updateClientName(long clientId, String name) throws Exception {
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("id", clientId);
        updateMap.put("name", name);
        return ormDao.updateOrm(UPDATE_CLIENT_NAME_QUERY, updateMap);
    }

    public int updateClientName2(long clientId, String name) throws Exception {
        return ormDao.updateSimpleOrm(UPDATE_CLIENT_NAME_QUERY, name, clientId);
    }

    public int updateClientName3(long clientId, String name) throws Exception {
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("name", name);
        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put("id", clientId);
        return ormDao.updateOrm(UPDATE_CLIENT_NAME_QUERY, updateMap, filterMap);
    }

    public int updateClientName4(ClientOrm client) throws Exception {
        return ormDao.updateOrm(UPDATE_CLIENT_NAME_QUERY, client, client);
    }

    private static final String UPDATE_DYNAMIC_CLIENT_QUERY =
            "UPDATE client " +
                    "   SET name = ::name, act_time = ::t#act_time, version = ::version " +
                    "       WHERE id = :id";

    public int updateDynamicClient(long clientId, Date newActTime) throws Exception {
        IUpdateValues mapValues = new UpdateMap("act_time", newActTime, "id", clientId);
        return ormDao.updateOrm(UPDATE_DYNAMIC_CLIENT_QUERY, mapValues);
    }

    public int updateDynamicClient2(ClientOrm newDataInClientOrm) throws Exception {
        return ormDao.updateOrm(UPDATE_DYNAMIC_CLIENT_QUERY, newDataInClientOrm);
    }
}