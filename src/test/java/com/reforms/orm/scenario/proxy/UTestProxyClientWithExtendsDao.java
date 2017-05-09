package com.reforms.orm.scenario.proxy;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

import com.reforms.orm.OrmDao;
import com.reforms.orm.scenario.TestScenarioDao;

/**
 * Тестируем выборку для dao с насдедованием
 * @author evgenie
 */
public class UTestProxyClientWithExtendsDao extends TestScenarioDao {

    public UTestProxyClientWithExtendsDao() {
        super("proxy.sql", "UTestProxyClientWithExtendsDao");
    }

    @Test
    public void testClientDaoUpdateClient() throws Exception {
        IClientWithExtendsDao clientDao = OrmDao.createDao(h2ds, IClientWithExtendsDao.class);
        ClientOrm client = new ClientOrm();
        client.setId(4L);
        client.setAddressId(4L);
        client.setName("Строгий Иван Сергеевич");
        client.setCity("Питер");
        client.setStreet("Новый");
        int count = clientDao.update(client);
        IClientDao finder = OrmDao.createDao(h2ds, IClientDao.class);
        assertEquals(1, count);
        ClientOrm clientOrm1 = finder.findClient(4L, null);
        assertEquals("[id=4, " +
                "name=Строгий Иван Сергеевич, " +
                "addressId=4, " +
                "city=Питер, " +
                "street=Новый, " +
                "actTime=Sun Jan 01 19:12:01 MSK 2017]", clientOrm1.toString());
    }

    @Test
    public void testClientDaoInsertClient() throws Exception {
        IClientWithExtendsDao clientDao = OrmDao.createDao(h2ds, IClientWithExtendsDao.class);
        ClientOrm client = new ClientOrm();
        client.setAddressId(6L);
        client.setCity("Питер");
        client.setStreet("Новый");
        client.setId(6L);
        client.setName("Сухой Иван Сергеевич");
        client.setActTime(new Date());
        clientDao.instert(client);
    }

    @Test
    public void testClientDaoDeleteClient() throws Exception {
        IClientWithExtendsDao clientDao = OrmDao.createDao(h2ds, IClientWithExtendsDao.class);
        clientDao.delete(5, 5);
    }
}