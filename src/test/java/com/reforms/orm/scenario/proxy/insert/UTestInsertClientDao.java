package com.reforms.orm.scenario.proxy.insert;

import com.reforms.orm.OrmDao;
import com.reforms.orm.scenario.TestScenarioDao;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Тестируем выборку для вложенных объектов
 * @author evgenie
 */
public class UTestInsertClientDao extends TestScenarioDao {

    public UTestInsertClientDao() {
        super("insert.sql", "UTestInsertClientDao");
    }

    @Test
    public void testInsertClientIntId() throws Exception {
        ClientOrm client = new ClientOrm();
        client.setName("First Client");
        IClientDao clientDao = OrmDao.createDao(h2ds, IClientDao.class);
        assertTrue(clientDao.insertClientAndGetIntId(client) != 0);
        assertTrue(clientDao.insertClientAndGetIntId(client) != 0);
        assertTrue(clientDao.insertClientAndGetIntId(client) != 0);
    }

    @Test
    public void testInsertClientLongId() throws Exception {
        ClientOrm client = new ClientOrm();
        client.setName("First Client");
        IClientDao clientDao = OrmDao.createDao(h2ds, IClientDao.class);
        assertTrue(clientDao.insertClientAndGetLongId(client) != 0);
        assertTrue(clientDao.insertClientAndGetLongId(client) != 0);
        assertTrue(clientDao.insertClientAndGetLongId(client) != 0);
    }

    @Test
    public void testInsertClientBigIntegerId() throws Exception {
        ClientOrm client = new ClientOrm();
        client.setName("First Client");
        IClientDao clientDao = OrmDao.createDao(h2ds, IClientDao.class);
        assertTrue(clientDao.insertClientAndGetBigIntegerId(client) != null);
        assertTrue(clientDao.insertClientAndGetBigIntegerId(client) != null);
        assertTrue(clientDao.insertClientAndGetBigIntegerId(client) != null);
    }

    @Test
    public void testInsertClientAndGetBigDecimalId() throws Exception {
        ClientOrm client = new ClientOrm();
        client.setName("First Client");
        IClientDao clientDao = OrmDao.createDao(h2ds, IClientDao.class);
        assertTrue(clientDao.insertClientAndGetBigDecimalId(client) != null);
        assertTrue(clientDao.insertClientAndGetBigDecimalId(client) != null);
        assertTrue(clientDao.insertClientAndGetBigDecimalId(client) != null);
    }

    @Test
    public void testInsertClientAndGetOkState() throws Exception {
        ClientOrm client = new ClientOrm();
        client.setName("First Client");
        IClientDao clientDao = OrmDao.createDao(h2ds, IClientDao.class);
        assertTrue(clientDao.insertClientAndGetOkState(client));
        assertTrue(clientDao.insertClientAndGetOkState(client));
    }
}