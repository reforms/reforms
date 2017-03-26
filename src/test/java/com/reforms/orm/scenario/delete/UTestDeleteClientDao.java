package com.reforms.orm.scenario.delete;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.reforms.orm.scenario.TestScenarioDao;

/**
 * Тестируем логику удаления
 *
 * @author evgenie
 */
public class UTestDeleteClientDao extends TestScenarioDao {

    public UTestDeleteClientDao() {
        super("scenario_delete.sql", "UTestDeleteClientDao");
    }

    @Test
    public void testDeleteClient() throws Exception {
        ClientDao clientDao = new ClientDao(h2ds);
        int count = clientDao.deleteClient(1L);
        assertEquals(1, count);
    }

    @Test
    public void testDeleteClient2() throws Exception {
        ClientDao clientDao = new ClientDao(h2ds);
        ClientOrm clientOrm = new ClientOrm();
        clientOrm.setVersion(17);
        int count = clientDao.deleteClient(clientOrm);
        assertEquals(1, count);
    }

    @Test
    public void testDeleteClient3() throws Exception {
        ClientDao clientDao = new ClientDao(h2ds);
        int count = clientDao.deleteClient(new long[]{2, 4}, 2);
        assertEquals(2, count);
    }
}
