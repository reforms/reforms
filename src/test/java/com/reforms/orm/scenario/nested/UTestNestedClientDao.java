package com.reforms.orm.scenario.nested;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.reforms.orm.scenario.TestScenarioDao;

/**
 * Тестируем выборку для вложенных объектов
 * @author evgenie
 */
public class UTestNestedClientDao extends TestScenarioDao {

    public UTestNestedClientDao() {
        super("scenario_nested.sql", "UTestClientDao");
    }

    @Test
    public void testClientDaoMap() throws Exception {
        ClientDao clientDao = new ClientDao(h2ds);
        ClientOrm clientOrm1 = clientDao.loadClient(1L);
        assertEquals("[clientId=1, " +
                      "clientName=Пупкин Иван Иванович, " +
                      "clientAddress=[addressId=1, " +
                                     "refCity=Москва, " +
                                     "refStreet=Лужники], " +
                      "logDate=2017-01-01 19:12:01.69]", clientOrm1.toString());
        ClientOrm clientOrm2 = clientDao.loadClient(2L);
        assertEquals("[clientId=2, " +
                      "clientName=Остапов Никалай Сергеевич, " +
                      "clientAddress=[addressId=2, " +
                                     "refCity=Москва, " +
                                     "refStreet=Конова], " +
                      "logDate=2017-01-01 19:12:01.69]", clientOrm2.toString());
    }

    @Test
    public void testClientsDaoMap() throws Exception {
        ClientDao clientDao = new ClientDao(h2ds);
        List<ClientOrm> clientsOrm = clientDao.loadClients();
        ClientOrm clientOrm1 = clientsOrm.get(0);
        assertEquals("[clientId=1, " +
                      "clientName=Пупкин Иван Иванович, " +
                      "clientAddress=[addressId=1, " +
                                     "refCity=Москва, " +
                                     "refStreet=Лужники], " +
                      "logDate=2017-01-01 19:12:01.69]", clientOrm1.toString());
        ClientOrm clientOrm2 = clientsOrm.get(1);
        assertEquals("[clientId=2, " +
                      "clientName=Остапов Никалай Сергеевич, " +
                      "clientAddress=[addressId=2, " +
                                     "refCity=Москва, " +
                                     "refStreet=Конова], " +
                      "logDate=2017-01-01 19:12:01.69]", clientOrm2.toString());
    }

}