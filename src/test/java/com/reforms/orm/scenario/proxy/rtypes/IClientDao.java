package com.reforms.orm.scenario.proxy.rtypes;

import static com.reforms.ann.TargetQuery.QT_SELECT;

import java.util.List;
import java.util.Set;

import com.reforms.ann.TargetFilter;
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

    @TargetQuery(ALL_CLIENTS_QUERY)
    public Client[] arrayClients();

    @TargetQuery(type = QT_SELECT, query = ALL_CLIENTS_QUERY, orm = Client.class)
    public void handleClients(OrmHandler<Client> handler);

    @TargetQuery(type = QT_SELECT, query = ALL_CLIENTS_QUERY, orm = Client.class)
    public OrmIterator<Client> iterateClients();

    @TargetQuery(query = ALL_CLIENTS_QUERY, orm = String.class)
    public List<String> listClientNames(@TargetFilter(columnFilter=true) int columnIndex);

    @TargetQuery(query = ALL_CLIENTS_QUERY, orm = String.class)
    public Set<String> setClientNames(@TargetFilter(columnFilter=true) int columnIndex);

    @TargetQuery(ALL_CLIENTS_QUERY)
    public String[] arrayClientNames(@TargetFilter(columnFilter=true) int columnIndex);

    static final String ALL_CLIENT_IDS_QUERY = "SELECT id FROM client ORDER BY id ASC";

    @TargetQuery(query = ALL_CLIENT_IDS_QUERY, orm = Long.class)
    public List<Long> listClientIds();

    @TargetQuery(query = ALL_CLIENT_IDS_QUERY, orm = Long.class)
    public Set<Long> setClientIds();

    @TargetQuery(ALL_CLIENT_IDS_QUERY)
    public Long[] arrayClientIds();

    @TargetQuery(query = ALL_CLIENT_IDS_QUERY, orm = ClientState.class)
    public List<ClientState> listClientState();

    @TargetQuery(query = ALL_CLIENT_IDS_QUERY, orm = ClientState.class)
    public Set<ClientState> setClientState();

    @TargetQuery(ALL_CLIENT_IDS_QUERY)
    public ClientState[] arrayClientState();
}