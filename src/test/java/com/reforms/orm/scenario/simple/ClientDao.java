package com.reforms.orm.scenario.simple;

import com.reforms.orm.H2DataSource;
import com.reforms.orm.OrmDao;

import java.util.List;

/**
 * Слой доступа к данным по клиенту
 * @author evgenie
 */
class ClientDao {

    private OrmDao ormDao;

    public ClientDao(H2DataSource h2ds) {
        ormDao = new OrmDao(h2ds);
    }

    private static final String LOAD_CLIENTS_QUERY =
            "SELECT cl.id AS l#, " +
            "       cl.nAme, " +
            "       addr.id AS address_Id, " +
            "       addr.cITy, " +
            "       addr.street, " +
            "       cl.act_time AS t# " +
            "    FROM client AS cl, " +
            "         address AS addr" +
            "        WHERE cl.address_id = addr.id AND " +
            "              cl.id = ::client_id_param" +
            "            ORDER BY cl.id ASC";

    public ClientOrm loadClient(long clientId) throws Exception {
        return ormDao.selectSimpleOrm(ClientOrm.class, LOAD_CLIENTS_QUERY, clientId);
    }

    public List<ClientOrm> loadClients() throws Exception {
        return ormDao.selectOrms(ClientOrm.class, LOAD_CLIENTS_QUERY);
    }
}