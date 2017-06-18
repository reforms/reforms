package com.reforms.orm.scenario.proxy.batch;

import com.reforms.ann.TargetDao;
import com.reforms.ann.TargetQuery;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Контракт на получение списка клиентов
 * @author evgenie
 */
@TargetDao(name = "Дао для работы с клиентом", orm = Client.class)
public interface IUpdateClientDao {

    static final String UPDATE_QUERY = "UPDATE client SET name = :name WHERE id = :id";

    @TargetQuery(query = UPDATE_QUERY, batchSize = 2)
    int[][] updateList(List<Client> clients);

    @TargetQuery(query = UPDATE_QUERY, batchSize = 2)
    int[][] updateSet(Set<Client> clients);

    @TargetQuery(query = UPDATE_QUERY, batchSize = 2)
    int[][] updateIterator(Iterator<Client> clients);

    @TargetQuery("SELECT id, name FROM client ORDER BY id ASC")
    public List<Client> listClients();

    static final String UPDATE_QUERY2 = "UPDATE client2 SET name = :s#name WHERE id = :id";

    @TargetQuery(query = UPDATE_QUERY2, batchSize = 2)
    int[][] updateListWithDirective(List<Client> clients);

    static final String UPDATE_QUERY3 = "UPDATE client2 SET name = :name WHERE id = :id";

    @TargetQuery(query = UPDATE_QUERY3, batchSize = 2)
    int[][] updateListWithoutDirective(List<Client> clients);

    static final String UPDATE_QUERY4 = "UPDATE client2 SET name = ? WHERE id = ?";

    @TargetQuery(query = UPDATE_QUERY4, batchSize = 2)
    int[][] updateIteratorWithQuestion(Iterator<Object[]> clients);

    @TargetQuery("SELECT id, name FROM client2 ORDER BY id ASC")
    public List<Client> listClients2();
}