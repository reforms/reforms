package com.reforms.orm.scenario.proxy.batch;

import static com.reforms.ann.TargetQuery.QT_SELECT;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.reforms.ann.TargetQuery;


/**
 * Контракт на получение списка клиентов
 * @author evgenie
 */
public interface IClientDao {

    static final String UPDATE_QUERY = "UPDATE client SET name = :name WHERE id = :id";

    @TargetQuery(query = UPDATE_QUERY, batchSize = 2, orm = Client.class)
    int[][] updateList(List<Client> clients);

    @TargetQuery(query = UPDATE_QUERY, batchSize = 2)
    int[][] updateSet(Set<Client> clients);

    @TargetQuery(query = UPDATE_QUERY, batchSize = 2)
    int[][] updateIterator(Iterator<Client> clients);

    static final String ALL_CLIENTS_QUERY = "SELECT id, name FROM client ORDER BY id ASC";

    @TargetQuery(type = QT_SELECT, query = ALL_CLIENTS_QUERY, orm = Client.class)
    public List<Client> listClients();


    static final String UPDATE_QUERY2 = "UPDATE client2 SET name = :s#name WHERE id = :id";

    @TargetQuery(query = UPDATE_QUERY2, batchSize = 2, orm = Client.class)
    int[][] updateListWithDirective(List<Client> clients);

    static final String UPDATE_QUERY3 = "UPDATE client2 SET name = :name WHERE id = :id";

    @TargetQuery(query = UPDATE_QUERY3, batchSize = 2, orm = Client.class)
    int[][] updateListWithoutDirective(List<Client> clients);

    static final String UPDATE_QUERY4 = "UPDATE client2 SET name = ? WHERE id = ?";

    @TargetQuery(query = UPDATE_QUERY4, batchSize = 2)
    int[][] updateIteratorWithQuestion(Iterator<Object[]> clients);

    @TargetQuery(type = QT_SELECT, query = "SELECT id, name FROM client2 ORDER BY id ASC", orm = Client.class)
    public List<Client> listClients2();
}