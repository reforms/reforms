package com.reforms.orm.scenario.proxy.rtypes;

import static org.junit.Assert.assertEquals;

import java.util.*;

import org.junit.Test;

import com.reforms.orm.OrmDao;
import com.reforms.orm.dao.bobj.model.OrmIterator;
import com.reforms.orm.scenario.TestScenarioDao;

/**
 * Тестируем выборку для вложенных объектов
 * @author evgenie
 */
public class UTestTypesClientDao extends TestScenarioDao {

    public UTestTypesClientDao() {
        super("rtypes.sql", "UTestTypesClientDao");
    }

    @Test
    public void testList() {
        IClientDao clientDao = OrmDao.createDao(h2ds, IClientDao.class);
        List<Client> clients = clientDao.listClients();
        assertClients(clients);
    }

    @Test
    public void testSet() {
        IClientDao clientDao = OrmDao.createDao(h2ds, IClientDao.class);
        Set<Client> clients = clientDao.setClients();
        assertClients(clients);
    }

    @Test
    public void testArray() {
        IClientDao clientDao = OrmDao.createDao(h2ds, IClientDao.class);
        Client[] clients = clientDao.arrayClients();
        assertClients(Arrays.asList(clients));
    }

    @Test
    public void testHandler() {
        IClientDao clientDao = OrmDao.createDao(h2ds, IClientDao.class);
        List<Client> clients = new ArrayList<>();
        clientDao.handleClients(clients::add);
        assertClients(clients);
    }

    @Test
    public void testIterate() throws Exception {
        IClientDao clientDao = OrmDao.createDao(h2ds, IClientDao.class);
        OrmIterator<Client> it = clientDao.iterateClients();
        List<Client> clients = new ArrayList<>();
        while (it.hasNext()) {
            clients.add(it.next());
        }
        assertClients(clients);
    }

    private void assertClients(Iterable<Client> it) {
        Map<Long, String> info = new HashMap<Long, String>();
        int index = 0;
        for (Client cl : it) {
            info.put(cl.getId(), cl.getName());
            index++;
        }
        assertEquals(3, index);
        assertEquals("1", info.get(1L));
        assertEquals("2", info.get(2L));
        assertEquals("3", info.get(3L));
    }

}