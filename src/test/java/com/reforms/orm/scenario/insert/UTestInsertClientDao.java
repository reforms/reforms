package com.reforms.orm.scenario.insert;

import static org.junit.Assert.assertEquals;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;

import org.junit.Test;

import com.reforms.orm.scenario.TestScenarioDao;

/**
 * Тестируем логику вставки
 *
 * @author evgenie
 */
public class UTestInsertClientDao extends TestScenarioDao {

    public UTestInsertClientDao() {
        super("scenario_insert.sql", "UTestInsertClientDao");
    }

    @Test
    public void testInsertClient() throws Exception {
        ClientOrm clientOrm = makeClient(2L, "Гагарин", "24.03.2017 13:10:10.121", 12);
        ClientDao clientDao = new ClientDao(h2ds);
        clientDao.insertClient(clientOrm);
        assertClient(2L, clientOrm);
    }

    @Test
    public void testInsertClien2t() throws Exception {
        ClientOrm clientOrm = makeClient(3L, "Поддубный", "25.03.2017 13:10:10.121", 13);
        ClientDao clientDao = new ClientDao(h2ds);
        clientDao.insertClient(clientOrm.getId(), clientOrm.getName(), clientOrm.getActTime(), clientOrm.getVersion());
        assertClient(3L, clientOrm);
    }

    private ClientOrm makeClient(long id, String name, String actTime, int version) {
        ClientOrm clientOrm = new ClientOrm();
        clientOrm.setId(id);
        clientOrm.setName(name);
        clientOrm.setActTime(new SimpleDateFormat("dd.MM.yyy HH:mm:ss.SSS").parse(actTime, new ParsePosition(0)));
        clientOrm.setVersion(version);
        return clientOrm;
    }

    private void assertClient(long clientId, ClientOrm expectedClient) throws Exception {
        ClientDao clientDao = new ClientDao(h2ds);
        ClientOrm actualClient = clientDao.loadClient(clientId);
        assertEquals(expectedClient.toString(), actualClient.toString());
    }
}
