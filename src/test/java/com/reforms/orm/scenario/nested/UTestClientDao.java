package com.reforms.orm.scenario.nested;

import com.reforms.orm.DataDbTest;
import com.reforms.orm.scenario.ScriptIterator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.reforms.orm.scenario.ScriptIterator.getResourceIterator;

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
        ClientOrm clientOrm = clientDao.loadClient(1L);
        System.out.println(clientOrm);
    }

    @After
    public void afterTest() {
        close();
    }
}
