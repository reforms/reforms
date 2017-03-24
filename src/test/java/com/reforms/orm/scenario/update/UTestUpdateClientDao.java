package com.reforms.orm.scenario.update;

import com.reforms.orm.scenario.TestScenarioDao;

import org.junit.Test;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * Тестируем выборку для получения списка простых объектов:
 * List<Integer>, List<String>, List<Date>
 *
 * @author evgenie
 */
public class UTestUpdateClientDao extends TestScenarioDao {

    public UTestUpdateClientDao() {
        super("scenario_update.sql", "UTestUpdateClientDao");
    }

    @Test
    public void testFullUpdateClient() throws Exception {
        ClientDao clientDao = new ClientDao(h2ds);
        ClientOrm expectedClient = makeClient(1L, "Пупкин", "24.03.2017 13:10:10.121", 2);
        int count = clientDao.updateFullClient(expectedClient);
        assertEquals(1, count);
        assertClient(expectedClient.getId(), expectedClient);
    }

    @Test
    public void testUpdateClientName() throws Exception {
        ClientOrm expectedClient = loadClient(1L);
        expectedClient.setName("Поддубный");
        ClientDao clientDao = new ClientDao(h2ds);
        int count = clientDao.updateClientName(expectedClient.getId(), expectedClient.getName());
        assertEquals(1, count);
        assertClient(expectedClient.getId(), expectedClient);
    }

    @Test
    public void testUpdateClientName2() throws Exception {
        ClientOrm expectedClient = loadClient(1L);
        expectedClient.setName("Гагарин");
        ClientDao clientDao = new ClientDao(h2ds);
        int count = clientDao.updateClientName2(expectedClient.getId(), expectedClient.getName());
        assertEquals(1, count);
        assertClient(expectedClient.getId(), expectedClient);
    }

    @Test
    public void testUpdateClientName3() throws Exception {
        ClientOrm expectedClient = loadClient(1L);
        expectedClient.setName("Попов");
        ClientDao clientDao = new ClientDao(h2ds);
        int count = clientDao.updateClientName3(expectedClient.getId(), expectedClient.getName());
        assertEquals(1, count);
        assertClient(expectedClient.getId(), expectedClient);
    }

    @Test
    public void testUpdateClientName4() throws Exception {
        ClientOrm expectedClient = loadClient(1L);
        expectedClient.setName("Зворыкин");
        ClientDao clientDao = new ClientDao(h2ds);
        int count = clientDao.updateClientName4(expectedClient);
        assertEquals(1, count);
        assertClient(expectedClient.getId(), expectedClient);
    }

    @Test
    public void testUpdateDynamicClient() throws Exception {
        ClientOrm expectedClient = loadClient(1L);
        Date newActTimeDate = new Date();
        ClientDao clientDao = new ClientDao(h2ds);
        int count = clientDao.updateDynamicClient(expectedClient.getId(), newActTimeDate);
        assertEquals(1, count);
        expectedClient.setActTime(newActTimeDate);
        assertClient(expectedClient.getId(), expectedClient);
    }

    @Test
    public void testUpdateDynamicClient2() throws Exception {
        ClientOrm expectedClient = loadClient(1L);
        Date newActTimeDate = new Date();
        ClientDao clientDao = new ClientDao(h2ds);
        ClientOrm newClientInfo = new ClientOrm();
        newClientInfo.setId(expectedClient.getId());
        newClientInfo.setActTime(newActTimeDate);
        int count = clientDao.updateDynamicClient2(newClientInfo);
        assertEquals(1, count);
        expectedClient.setActTime(newActTimeDate);
        assertClient(expectedClient.getId(), expectedClient);
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
        ClientOrm actualClient = loadClient(clientId);
        assertEquals(expectedClient.toString(), actualClient.toString());
    }

    private ClientOrm loadClient(long clientId) throws Exception {
        ClientDao clientDao = new ClientDao(h2ds);
        ClientOrm actualClient = clientDao.loadClient(clientId);
        return actualClient;
    }
}