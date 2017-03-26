package com.reforms.orm.scenario.delete;

import com.reforms.orm.H2DataSource;
import com.reforms.orm.OrmDao;
import com.reforms.orm.dao.filter.FilterObject;

/**
 * Слой доступа к данным по
 * @author evgenie
 */
public class ClientDao {

    private OrmDao ormDao;

    public ClientDao(H2DataSource h2ds) {
        ormDao = new OrmDao(h2ds);
    }

    private static final String DELETE_CLIENT_QUERY =
            "DELETE FROM client WHERE id = ?";

    public int deleteClient(long id) throws Exception {
        return ormDao.delete(DELETE_CLIENT_QUERY, id);
    }

    private static final String DELETE_CLIENT_QUERY2 =
            "DELETE FROM client WHERE id = ::id AND version = ::version";

    public int deleteClient(ClientOrm client) throws Exception {
        return ormDao.deleteOrm(DELETE_CLIENT_QUERY2, new FilterObject(client));
    }

    private static final String DELETE_CLIENT_QUERY3 =
            "DELETE FROM client WHERE id IN (:ids) AND version = :version";

    public int deleteClient(long[] ids, int version) throws Exception {
        return ormDao.delete(DELETE_CLIENT_QUERY3, ids, version);
    }

}