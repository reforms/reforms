package com.reforms.orm.scenario.insert;

import com.reforms.orm.H2DataSource;
import com.reforms.orm.OrmDao;
import com.reforms.orm.dao.bobj.update.UpdateObject;

import java.util.Date;

/**
 * Слой доступа к данным по
 * @author evgenie
 */
class ClientDao {

    private final OrmDao ormDao;

    public ClientDao(H2DataSource h2ds) {
        ormDao = new OrmDao(h2ds);
    }

    private static final String INSERT_CLIENT_QUERY =
            "INSERT INTO client (id, name, act_time, version) VALUES(:id, :name, :t#act_time, :version)";

    public void insertClient(ClientOrm client) throws Exception {
        ormDao.insertOrm(INSERT_CLIENT_QUERY, new UpdateObject(client));
    }

    public void insertClient(long clientId, String name, Date date, int version) throws Exception {
        ormDao.insert(INSERT_CLIENT_QUERY, clientId, name, date, version);
    }

    private static final String LOAD_CLIENT_QUERY =
            "SELECT id, name, act_time t#, version " +
            "    FROM client " +
            "        WHERE id = ? " +
            "           ORDER BY id ASC";

    public ClientOrm loadClient(long id) throws Exception {
        return ormDao.select(ClientOrm.class, LOAD_CLIENT_QUERY, id);
    }


}