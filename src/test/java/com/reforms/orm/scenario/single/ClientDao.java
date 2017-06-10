package com.reforms.orm.scenario.single;

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

    private static final String LOAD_CLIENT_IDS_QUERY =
            "SELECT cl.id FROM client AS cl ORDER BY 1 ASC";

    public List<Long> loadClientIds() throws Exception {
        return ormDao.selectSimpleOrms(Long.class, LOAD_CLIENT_IDS_QUERY);
    }

    private static final String LOAD_CLIENT_NAMES_QUERY =
            "SELECT cl.name AS clientName FROM client AS cl ORDER BY 1 ASC";

    public List<String> loadClientNames() throws Exception {
        return ormDao.selectOrms(String.class, LOAD_CLIENT_NAMES_QUERY);
    }

    private static final String LOAD_CLIENT_CLIENT_ORDER_QUERY =
            "SELECT cl.id FROM client AS cl ORDER BY 1 ASC";

    public List<ClientOrder> loadClientOrders() throws Exception {
        return ormDao.selectOrms(ClientOrder.class, LOAD_CLIENT_CLIENT_ORDER_QUERY);
    }
}