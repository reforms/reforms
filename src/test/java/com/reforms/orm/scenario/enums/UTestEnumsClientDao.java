package com.reforms.orm.scenario.enums;

import com.reforms.orm.scenario.TestScenarioDao;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Тестируем выборку для вложенных объектов
 * @author evgenie
 */
public class UTestEnumsClientDao extends TestScenarioDao {

    public UTestEnumsClientDao() {
        super("scenario_enums.sql", "UTestEnumsClientDao");
    }

    @Test
    public void testClientDaoMap() throws Exception {
        ClientDao clientDao = new ClientDao(h2ds);
        ClientOrm clientOrm1 = clientDao.loadClient(ClientType.PERSON);
        assertEquals("[id=1, " +
                      "type=PERSON, " +
                      "state=ACTIVE, " +
                      "mode=online]", clientOrm1.toString());
        ClientOrm clientOrm2 = clientDao.loadClient(ClientType.COMPANY, ClientMode.ONLINE);
        assertEquals("[id=2, " +
                      "type=COMPANY, " +
                      "state=ACTIVE, " +
                      "mode=online]", clientOrm2.toString());

        ClientOrm clientOrm3 = clientDao.loadClient(ClientState.BLOCKED, ClientMode.OFFLINE);
        assertEquals("[id=3, " +
                      "type=COMPANY, " +
                      "state=BLOCKED, " +
                      "mode=offline]", clientOrm3.toString());
    }

    @Test
    public void testClientDaoMap2() throws Exception {
        ClientDao clientDao = new ClientDao(h2ds);
        ClientOrm clientOrm1 = clientDao.loadClient2(ClientType.PERSON);
        assertEquals("[id=1, " +
                "type=PERSON, " +
                "state=ACTIVE, " +
                "mode=online]", clientOrm1.toString());
        ClientOrm clientOrm2 = clientDao.loadClient2(ClientType.COMPANY, ClientMode.ONLINE);
        assertEquals("[id=2, " +
                "type=COMPANY, " +
                "state=ACTIVE, " +
                "mode=online]", clientOrm2.toString());
        ClientOrm clientOrm3 = clientDao.loadClient2(ClientState.BLOCKED, ClientMode.OFFLINE);
        assertEquals("[id=3, " +
                "type=COMPANY, " +
                "state=BLOCKED, " +
                "mode=offline]", clientOrm3.toString());
    }
}