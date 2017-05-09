package com.reforms.orm.scenario.proxy;

import com.reforms.orm.OrmDao;
import com.reforms.orm.scenario.TestScenarioDao;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Тестируем выборку для вложенных объектов
 * @author evgenie
 */
public class UTestSimpleClientDao extends TestScenarioDao {

    public UTestSimpleClientDao() {
        super("proxy.sql", "UTestSimpleClientDao");
    }

    @Test
    public void testClientDaoLoadClients() throws Exception {
        IClientDao clientDao = OrmDao.createDao(h2ds, IClientDao.class);
        List<ClientOrm> clients = clientDao.loadClients(new SimpleDateFormat("dd.MM.yyyy").parse("10.10.2016"));
        ClientOrm clientOrm1 = clients.get(0);
        assertEquals("[id=1, " +
                "name=Пупкин Иван Иванович, " +
                      "addressId=1, " +
                "city=Москва, " +
                "street=Лужники, " +
                "actTime=Sun Jan 01 19:12:01 MSK 2017]", clientOrm1.toString());
        ClientOrm clientOrm2 = clients.get(1);
        assertEquals("[id=2, " +
                "name=Остапов Никалай Сергеевич, " +
                      "addressId=2, " +
                "city=Москва, " +
                "street=Конова, " +
                "actTime=Sun Jan 01 19:12:01 MSK 2017]", clientOrm2.toString());
    }

    @Test
    public void testClientDaoFindClient() throws Exception {
        IClientDao clientDao = OrmDao.createDao(h2ds, IClientDao.class);
        ClientOrm clientOrm1 = clientDao.findClient(1L, new SimpleDateFormat("dd.MM.yyyy").parse("10.10.2016"));
        assertEquals("[id=1, " +
                "name=Пупкин Иван Иванович, " +
                      "addressId=1, " +
                "city=Москва, " +
                "street=Лужники, " +
                "actTime=Sun Jan 01 19:12:01 MSK 2017]", clientOrm1.toString());
    }

    @Test
    public void testClientDaoLoadClients1() throws Exception {
        IClientDao clientDao = OrmDao.createDao(h2ds, IClientDao.class);
        List<ClientOrm> clients = clientDao.loadClients1(new SimpleDateFormat("dd.MM.yyyy").parse("10.10.2016"));
        ClientOrm clientOrm1 = clients.get(0);
        assertEquals("[id=1, " +
                "name=Пупкин Иван Иванович, " +
                      "addressId=1, " +
                "city=Москва, " +
                "street=Лужники, " +
                "actTime=Sun Jan 01 19:12:01 MSK 2017]", clientOrm1.toString());
        ClientOrm clientOrm2 = clients.get(1);
        assertEquals("[id=2, " +
                "name=Остапов Никалай Сергеевич, " +
                      "addressId=2, " +
                "city=Москва, " +
                "street=Конова, " +
                "actTime=Sun Jan 01 19:12:01 MSK 2017]", clientOrm2.toString());
    }

    @Test
    public void testClientDaoFindClient1() throws Exception {
        IClientDao clientDao = OrmDao.createDao(h2ds, IClientDao.class);
        ClientOrm clientOrm1 = clientDao.findClient1(1L, new SimpleDateFormat("dd.MM.yyyy").parse("10.10.2016"));
        assertEquals("[id=1, " +
                "name=Пупкин Иван Иванович, " +
                      "addressId=1, " +
                "city=Москва, " +
                "street=Лужники, " +
                "actTime=Sun Jan 01 19:12:01 MSK 2017]", clientOrm1.toString());
    }

    @Test
    public void testClientDaoFindClient2() throws Exception {
        IClientDao clientDao = OrmDao.createDao(h2ds, IClientDao.class);
        Map<String, Object> filters = new HashMap<>();
        filters.put("client_d", 1L);
        filters.put("act_time", new SimpleDateFormat("dd.MM.yyyy").parse("10.10.2016"));
        ClientOrm clientOrm1 = clientDao.findClient2(filters);
        assertEquals("[id=1, " +
                "name=Пупкин Иван Иванович, " +
                      "addressId=1, " +
                "city=Москва, " +
                "street=Лужники, " +
                "actTime=Sun Jan 01 19:12:01 MSK 2017]", clientOrm1.toString());
    }

    @Test
    public void testClientDaoFindClient3() throws Exception {
        IClientDao clientDao = OrmDao.createDao(h2ds, IClientDao.class);
        ClientFilter clientFilter = new ClientFilter(1L, new SimpleDateFormat("dd.MM.yyyy").parse("10.10.2016"));
        ClientOrm clientOrm1 = clientDao.findClient2(clientFilter);
        assertEquals("[id=1, " +
                "name=Пупкин Иван Иванович, " +
                      "addressId=1, " +
                "city=Москва, " +
                "street=Лужники, " +
                "actTime=Sun Jan 01 19:12:01 MSK 2017]", clientOrm1.toString());
    }

    @Test
    public void testClientDaoUpdateClient1() throws Exception {
        IClientDao clientDao = OrmDao.createDao(h2ds, IClientDao.class);
        int count = clientDao.updateClient("Бодрый Иван Сергеевич", 3L);
        assertEquals(1, count);
        ClientOrm clientOrm1 = clientDao.findClient(3L, null);
        assertEquals("[id=3, " +
                "name=Бодрый Иван Сергеевич, " +
                "addressId=3, " +
                "city=Москва, " +
                "street=Кантемировская, " +
                "actTime=Sun Jan 01 19:12:01 MSK 2017]", clientOrm1.toString());
    }

    @Test
    public void testClientDaoUpdateClient2() throws Exception {
        IClientDao clientDao = OrmDao.createDao(h2ds, IClientDao.class);
        ClientOrm client = new ClientOrm();
        client.setId(4L);
        client.setAddressId(4L);
        client.setName("Строгий Иван Сергеевич");
        client.setCity("Питер");
        client.setStreet("Новый");
        int count = clientDao.update(client);
        assertEquals(1, count);
        ClientOrm clientOrm1 = clientDao.findClient(4L, null);
        System.out.println(clientOrm1);
        assertEquals("[id=4, " +
                "name=Строгий Иван Сергеевич, " +
                "addressId=4, " +
                "city=Питер, " +
                "street=Новый, " +
                "actTime=Sun Jan 01 19:12:01 MSK 2017]", clientOrm1.toString());
    }

    @Test
    public void testClientDaoDeleteClient2() throws Exception {
        IClientDao clientDao = OrmDao.createDao(h2ds, IClientDao.class);
        ClientOrm client = new ClientOrm();
        client.setAddressId(6L);
        client.setCity("Питер");
        client.setStreet("Новый");
        client.setId(6L);
        client.setName("Сухой Иван Сергеевич");
        client.setActTime(new Date());
        clientDao.instert(client);
    }
}