package com.reforms.orm.scenario.proxy.batch;

import com.reforms.orm.OrmDao;
import com.reforms.orm.scenario.TestScenarioDao;

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
public class UTestUpdateBatchClientDao extends TestScenarioDao {

    public UTestUpdateBatchClientDao() {
        super("batch_update.sql", "UTestUpdateBatchClientDao");
    }

    @Test
    public void testList() {
        IUpdateClientDao clientDao = OrmDao.createDao(h2ds, IUpdateClientDao.class);
        List<Client> clients = makeClients();
        int[][] result = clientDao.updateList(clients);
        assertResult((clients.size() + 1) / 2, result);
        assertClients(clientDao.listClients(), clients);
    }

    private List<Client> makeClients() {
        List<Client> clients = new ArrayList<>();
        for (int index = 1; index <= 7; index++) {
            clients.add(new Client(index, String.valueOf(index + 10)));
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

    @Test
    public void testListWithDirective() {
        IUpdateClientDao clientDao = OrmDao.createDao(h2ds, IUpdateClientDao.class);
        List<Client> clients = makeClients2(10);
        int[][] result = clientDao.updateListWithDirective(clients);
        assertResult((clients.size() + 1) / 2, result);
        assertClients(clientDao.listClients2(), clients);
    }

    @Test
    public void testListWithoutDirective() {
        IUpdateClientDao clientDao = OrmDao.createDao(h2ds, IUpdateClientDao.class);
        List<Client> clients = makeClients2(20);
        int[][] result = clientDao.updateListWithoutDirective(clients);
        assertResult((clients.size() + 1) / 2, result);
        assertClients(clientDao.listClients2(), clients);
    }

    private List<Client> makeClients2(int offset) {
        List<Client> clients = new ArrayList<>();
        clients.add(new Client(1, null));
        clients.add(new Client(2, String.valueOf(offset)));
        clients.add(new Client(3, String.valueOf(offset + 1)));
        return clients;
    }

    @Test
    public void testUpdateIteratorWithQuestion() {
        IUpdateClientDao clientDao = OrmDao.createDao(h2ds, IUpdateClientDao.class);
        List<Object[]> clients = makeClients3();
        int[][] result = clientDao.updateIteratorWithQuestion(clients.iterator());
        assertResult((clients.size() + 1) / 2, result);
        List<Client> eClients = clients.stream()
                .map(item -> new Client((Integer)item[1], (String) item[0]))
                .collect(Collectors.toList());
        assertClients(clientDao.listClients2(), eClients);
    }

    private List<Object[]> makeClients3() {
        List<Object[]> clients = new ArrayList<>();
        clients.add(new Object[] { null, 1 });
        clients.add(new Object[] { "10", 2 });
        clients.add(new Object[] { null, 3 });
        return clients;
    }
}