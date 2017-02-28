package com.reforms.orm.scenario.enums;

import com.reforms.orm.H2DataSource;
import com.reforms.orm.OrmDao;

/**
 * Слой доступа к данным по клиенту
 * @author evgenie
 */
public class ClientDao {

    private OrmDao ormDao;

    public ClientDao(H2DataSource h2ds) {
        ormDao = new OrmDao(h2ds);
    }

    private static final String LOAD_CLIENT_QUERY_1 =
            "SELECT id, " +
            "       type e#, " +
            "       state e#, " +
            "       mode e#" +
            "    FROM client AS cl" +
            "        WHERE cl.type = ::client_type";

    /**
     * Загрузить информацию о клиенте
     * @param clientId
     * @return
     */
    public ClientOrm loadClient(ClientType clientType) throws Exception {
        return ormDao.loadSimpleOrm(ClientOrm.class, LOAD_CLIENT_QUERY_1, clientType);
    }

    private static final String LOAD_CLIENT_QUERY_2 =
            "SELECT id, " +
            "       type e#, " +
            "       state e#, " +
            "       mode e#" +
            "    FROM client AS cl" +
            "        WHERE cl.type = ::client_type AND " +
            "              cl.mode = ::client_mode";

    /**
     * Загрузить информацию о клиенте
     * @param clientId
     * @return
     */
    public ClientOrm loadClient(ClientType clientType, ClientMode clientMode) throws Exception {
        return ormDao.loadSimpleOrm(ClientOrm.class, LOAD_CLIENT_QUERY_2, clientType, clientMode);
    }

    private static final String LOAD_CLIENT_QUERY_3 =
            "SELECT id, " +
            "       type e#, " +
            "       state e#, " +
            "       mode e#" +
            "    FROM client AS cl" +
            "        WHERE cl.state = ::client_state AND " +
            "              cl.mode = ::client_mode";

    /**
     * Загрузить информацию о клиенте
     * @param clientId
     * @return
     */
    public ClientOrm loadClient(ClientState clientState, ClientMode clientMode) throws Exception {
        return ormDao.loadSimpleOrm(ClientOrm.class, LOAD_CLIENT_QUERY_3, clientState, clientMode);
    }

    private static final String LOAD_CLIENT_QUERY_4 =
            "SELECT id, " +
            "       type, " +
            "       state, " +
            "       mode" +
            "    FROM client AS cl" +
            "        WHERE cl.type = ::client_type";

    /**
     * Загрузить информацию о клиенте
     * @param clientId
     * @return
     */
    public ClientOrm loadClient2(ClientType clientType) throws Exception {
        return ormDao.loadSimpleOrm(ClientOrm.class, LOAD_CLIENT_QUERY_4, clientType);
    }

    private static final String LOAD_CLIENT_QUERY_5 =
            "SELECT id, " +
            "       type, " +
            "       state, " +
            "       mode" +
            "    FROM client AS cl" +
            "        WHERE cl.type = ::client_type AND " +
            "              cl.mode = ::client_mode";

    /**
     * Загрузить информацию о клиенте
     * @param clientId
     * @return
     */
    public ClientOrm loadClient2(ClientType clientType, ClientMode clientMode) throws Exception {
        return ormDao.loadSimpleOrm(ClientOrm.class, LOAD_CLIENT_QUERY_5, clientType, clientMode);
    }

    private static final String LOAD_CLIENT_QUERY_6 =
            "SELECT id, " +
            "       type, " +
            "       state, " +
            "       mode" +
            "    FROM client AS cl" +
            "        WHERE cl.state = ::client_state AND " +
            "              cl.mode = ::client_mode";

    /**
     * Загрузить информацию о клиенте
     * @param clientId
     * @return
     */
    public ClientOrm loadClient2(ClientState clientState, ClientMode clientMode) throws Exception {
        return ormDao.loadSimpleOrm(ClientOrm.class, LOAD_CLIENT_QUERY_6, clientState, clientMode);
    }

}