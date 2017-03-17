package com.reforms.orm.scenario.single;

import com.reforms.orm.scenario.TestScenarioDao;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Тестируем выборку для получения списка простых объектов:
 * List<Integer>, List<String>, List<Date>
 *
 * @author evgenie
 */
public class UTestSingleClientDao extends TestScenarioDao {

    public UTestSingleClientDao() {
        super("scenario_single.sql", "UTestSingleClientDao");
    }

    @Test
    public void testLoadClientIdsDao() throws Exception {
        ClientDao clientDao = new ClientDao(h2ds);
        List<Long> clientIds = clientDao.loadClientIds();
        assertEquals("[1, 2]", clientIds.toString());
    }

    @Test
    public void testLoadClientNamesDao() throws Exception {
        ClientDao clientDao = new ClientDao(h2ds);
        List<String> clientNames = clientDao.loadClientNames();
        assertEquals("[Остапов Никалай Сергеевич, Пупкин Иван Иванович]", clientNames.toString());
    }

    @Test
    public void testLoadClientOrdersDao() throws Exception {
        ClientDao clientDao = new ClientDao(h2ds);
        List<ClientOrder> clientOrders = clientDao.loadClientOrders();
        assertEquals("[1, 2]", clientOrders.toString());
    }
}