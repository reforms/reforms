package com.reforms.orm.scenario.nested;

import com.reforms.orm.DataDbTest;
import com.reforms.orm.scenario.ScriptIterator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.reforms.orm.scenario.ScriptIterator.getResourceIterator;
import static org.junit.Assert.assertEquals;

/**
 * Тестируем выборку для вложенных объектов
 * @author evgenie
 */
public class UTestClientDao extends DataDbTest {

    public UTestClientDao() {
        super("UTestClientDao");
    }

    @Before
    public void beforeTest() throws Exception {
        ScriptIterator si = getResourceIterator("scenario_001.sql", getClass());
        while (si.hasNext()) {
            String statement = si.next();
            invokeStatement(statement);
        }
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

    @After
    public void afterTest() {
        close();
    }
}