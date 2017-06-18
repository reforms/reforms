package com.reforms.orm.scenario.proxy.batch;

import com.reforms.ann.TargetQuery;

import java.util.List;

import static com.reforms.ann.TargetQuery.QT_SELECT;


/**
 * Контракт на получение списка клиентов
 * @author evgenie
 */
public interface IInsertClientDao {

    static final String INSERT_NAMED_QUERY = "INSERT INTO client (id, name, state) VALUES(:id, :name, :state)";

    @TargetQuery(query = INSERT_NAMED_QUERY, batchSize = 2)
    int[][] insertList(List<Client> clients);

    static final String INSERT_QUEST_QUERY = "INSERT INTO client (id, name, state) VALUES(?, ?, ?)";

    @TargetQuery(query = INSERT_NAMED_QUERY, batchSize = 2)
    int[][] insertQuestList(List<Object[]> clients);

    static final String ALL_CLIENTS_QUERY = "SELECT id, name, state FROM client ORDER BY id ASC";

    @TargetQuery(type = QT_SELECT, query = ALL_CLIENTS_QUERY, orm = Client.class)
    public List<Client> listClients();

    @TargetQuery("DELETE FROM client")
    public void deleteAllClients();
}