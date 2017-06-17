package com.reforms.orm.scenario.proxy.rtypes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

    @Test
    public void testListId() {
        IClientDao clientDao = OrmDao.createDao(h2ds, IClientDao.class);
        List<Long> ids = clientDao.listClientIds();
        assertClientIds(ids);
    }

    @Test
    public void testSetId() {
        IClientDao clientDao = OrmDao.createDao(h2ds, IClientDao.class);
        Set<Long> ids = clientDao.setClientIds();
        assertClientIds(ids);
    }

    @Test
    public void testArrayId() {
        IClientDao clientDao = OrmDao.createDao(h2ds, IClientDao.class);
        Long[] ids = clientDao.arrayClientIds();
        assertClientIds(Arrays.asList(ids));
    }

    private void assertClientIds(Iterable<Long> it) {
        Set<Long> info = new HashSet<>();
        int index = 0;
        for (Long clId : it) {
            info.add(clId);
            index++;
        }
        assertEquals(3, index);
        assertTrue(info.remove(1L));
        assertTrue(info.remove(2L));
        assertTrue(info.remove(3L));
    }

    @Test
    public void testListNames() {
        IClientDao clientDao = OrmDao.createDao(h2ds, IClientDao.class);
        List<String> ids = clientDao.listClientNames(2);
        assertClientNames(ids);
    }

    @Test
    public void testSetNames() {
        IClientDao clientDao = OrmDao.createDao(h2ds, IClientDao.class);
        Set<String> names = clientDao.setClientNames(2);
        assertClientNames(names);
    }

    @Test
    public void testArrayNames() {
        IClientDao clientDao = OrmDao.createDao(h2ds, IClientDao.class);
        String[] names = clientDao.arrayClientNames(2);
        assertClientNames(Arrays.asList(names));
    }

    private void assertClientNames(Iterable<String> it) {
        Set<String> info = new HashSet<>();
        int index = 0;
        for (String clName : it) {
            info.add(clName);
            index++;
        }
        assertEquals(3, index);
        assertTrue(info.remove("1"));
        assertTrue(info.remove("2"));
        assertTrue(info.remove("3"));
    }

    @Test
    public void testListState() {
        IClientDao clientDao = OrmDao.createDao(h2ds, IClientDao.class);
        List<ClientState> states = clientDao.listClientState();
        assertClientState(states);
    }

    @Test
    public void testSetState() {
        IClientDao clientDao = OrmDao.createDao(h2ds, IClientDao.class);
        Set<ClientState> states = clientDao.setClientState();
        assertClientState(states);
    }

    @Test
    public void testArrayState() {
        IClientDao clientDao = OrmDao.createDao(h2ds, IClientDao.class);
        ClientState[] states = clientDao.arrayClientState();
        assertClientState(Arrays.asList(states));
    }

    private void assertClientState(Iterable<ClientState> it) {
        Set<ClientState> info = new HashSet<>();
        int index = 0;
        for (ClientState state : it) {
            info.add(state);
            index++;
        }
        assertEquals(3, index);
        assertTrue(info.remove(ClientState.ACTIVE));
        assertTrue(info.remove(ClientState.BLOCKED));
        assertTrue(info.remove(ClientState.DELETED));
    }
}