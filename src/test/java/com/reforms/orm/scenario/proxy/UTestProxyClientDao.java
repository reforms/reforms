package com.reforms.orm.scenario.proxy;

import com.reforms.orm.OrmDao;
import com.reforms.orm.dao.filter.column.FilterState;
import com.reforms.orm.dao.filter.column.ISelectedColumnFilter;
import com.reforms.orm.scenario.TestScenarioDao;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Тестируем выборку для вложенных объектов
 * @author evgenie
 */
public class UTestProxyClientDao extends TestScenarioDao {

    public UTestProxyClientDao() {
        super("proxy.sql", "UTestProxyClientDao");
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
    public void testClientDaoColumnFilter() throws Exception {
        IClientDao clientDao = OrmDao.createDao(h2ds, IClientDao.class);
        Date actTime = new SimpleDateFormat("dd.MM.yyyy").parse("10.10.2016");
        ISelectedColumnFilter keep2columns = column -> {
            return column.getIndex() <= 2 ? FilterState.FS_ACCEPT : FilterState.FS_REMOVE;
        };
        List<ClientOrm> clients = clientDao.loadClients(keep2columns, actTime);
        ClientOrm clientOrm1 = clients.get(0);
        // selected first 2 columns
        assertEquals(1L, clientOrm1.getId());
        assertEquals("Пупкин Иван Иванович", clientOrm1.getName());
        // don't seleceted -> to be zero and null
        assertEquals(0L, clientOrm1.getAddressId());
        assertNull(clientOrm1.getCity());
        assertNull(clientOrm1.getStreet());
        assertNull(clientOrm1.getActTime());
    }

    @Test
    public void testClientDaoColumnIntFilter() throws Exception {
        IClientDao clientDao = OrmDao.createDao(h2ds, IClientDao.class);
        Date actTime = new SimpleDateFormat("dd.MM.yyyy").parse("10.10.2016");
        List<ClientOrm> clients = clientDao.loadClients(new int[] {1, 2}, actTime);
        ClientOrm clientOrm1 = clients.get(0);
        // selected first 2 columns
        assertEquals(1L, clientOrm1.getId());
        assertEquals("Пупкин Иван Иванович", clientOrm1.getName());
        // don't seleceted -> to be zero and null
        assertEquals(0L, clientOrm1.getAddressId());
        assertNull(clientOrm1.getCity());
        assertNull(clientOrm1.getStreet());
        assertNull(clientOrm1.getActTime());

        clients = clientDao.loadClients(new int[] {4, 5}, actTime);
        clientOrm1 = clients.get(0);
        // selected first 4 and 5 columns
        assertEquals(0, clientOrm1.getId());
        assertNull(clientOrm1.getName());
        assertEquals(0L, clientOrm1.getAddressId());
        assertEquals("Москва", clientOrm1.getCity()); // 4
        assertEquals("Лужники", clientOrm1.getStreet()); // 5
        assertNull(clientOrm1.getActTime());
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
        assertEquals("[id=4, " +
                "name=Строгий Иван Сергеевич, " +
                "addressId=4, " +
                "city=Питер, " +
                "street=Новый, " +
                "actTime=Sun Jan 01 19:12:01 MSK 2017]", clientOrm1.toString());
    }

    @Test
    public void testClientDaoInsertClient() throws Exception {
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

    @Test
    public void testClientDaoDeleteClient() throws Exception {
        IClientDao clientDao = OrmDao.createDao(h2ds, IClientDao.class);
        clientDao.delete(5, 5);
    }
}