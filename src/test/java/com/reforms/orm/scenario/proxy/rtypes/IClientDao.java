package com.reforms.orm.scenario.proxy.rtypes;

import static com.reforms.ann.TargetQuery.QT_SELECT;

import java.util.List;
import java.util.Set;

import com.reforms.ann.TargetQuery;
import com.reforms.orm.dao.bobj.model.OrmHandler;
import com.reforms.orm.dao.bobj.model.OrmIterator;

/**
 * Контракт на получение списка клиентов
 * @author evgenie
 */
public interface IClientDao {

    static final String ALL_CLIENTS_QUERY = "SELECT id, name FROM client ORDER BY id ASC";

    @TargetQuery(type = QT_SELECT, query = ALL_CLIENTS_QUERY, orm = Client.class)
    public List<Client> listClients();

    @TargetQuery(type = QT_SELECT, query = ALL_CLIENTS_QUERY, orm = Client.class)
    public Set<Client> setClients();

    @TargetQuery(type = QT_SELECT, query = ALL_CLIENTS_QUERY)
    public Client[] arrayClients();

    @TargetQuery(type = QT_SELECT, query = ALL_CLIENTS_QUERY, orm = Client.class)
    public void handleClients(OrmHandler<Client> handler);

    @TargetQuery(type = QT_SELECT, query = ALL_CLIENTS_QUERY, orm = Client.class)
    public OrmIterator<Client> iterateClients();
}











