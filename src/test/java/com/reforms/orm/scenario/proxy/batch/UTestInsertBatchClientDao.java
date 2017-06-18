package com.reforms.orm.scenario.proxy.batch;

import com.reforms.orm.OrmDao;
import com.reforms.orm.scenario.TestScenarioDao;
import com.reforms.orm.scenario.proxy.rtypes.ClientState;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

/**
 * Тестируем выборку для вложенных объектов
 * @author evgenie
 */
public class UTestInsertBatchClientDao extends TestScenarioDao {

    public UTestInsertBatchClientDao() {
        super("batch_insert.sql", "UTestInsertBatchClientDao");
    }

    @Test
    public void testList() {
        IInsertClientDao clientDao = OrmDao.createDao(h2ds, IInsertClientDao.class);
        clientDao.deleteAllClients();
        List<Client> clients = makeClients();
        int[][] result = clientDao.insertList(clients);
        assertResult((clients.size() + 1) / 2, result);
        assertClients(clientDao.listClients(), clients);
    }

    @Test
    public void testQuestList() {
        IInsertClientDao clientDao = OrmDao.createDao(h2ds, IInsertClientDao.class);
        clientDao.deleteAllClients();
        List<Client> clients = makeClients();
        List<Object[]> clientsObj = clients.stream()
                .map(client -> new Object[]{client.getId(), client.getName()})
                .collect(Collectors.toList());
        int[][] result = clientDao.insertQuestList(clientsObj);
        assertResult((clients.size() + 1) / 2, result);
        assertClients(clientDao.listClients(), clients);
    }

    private List<Client> makeClients() {
        List<Client> clients = new ArrayList<>();
        for (int index = 1; index <= 7; index++) {
            if (index == 5) {
                clients.add(new Client(index, null, null));
            } else if (index == 6) {
                clients.add(new Client(index, "-2", null));
            } else if (index == 7) {
                clients.add(new Client(index, null, ClientState.BLOCKED));
            } else  {
                clients.add(new Client(index, String.valueOf(index + 10), ClientState.ACTIVE));
            }

        }
        return clients;
    }

    private void assertResult(int total, int[][] result) {
        assertEquals(total, result.length);
        for (int index = 0; index < total; index++) {
            int[] data = result[index];
            if (index != total - 1) {
                assertEquals(2, data.length);
                assertEquals(1, data[0]);
                assertEquals(1, data[0]);
            } else {
                assertEquals(1, data.length);
                assertEquals(1, data[0]);
            }
        }
    }

    private void assertClients(Iterable<Client> it, List<Client> expectedClients) {
        Map<Integer, String> info = new HashMap<>();
        int index = 0;
        for (Client cl : it) {
            info.put(cl.getId(), cl.getName());
            index++;
        }
        assertEquals(info.size(), index);
        for (Client client : expectedClients) {
            assertEquals(info.get(client.getId()), client.getName());
        }
    }
}