package com.reforms.orm.scenario.proxy;

import static com.reforms.ann.TargetQuery.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.reforms.ann.TargetFilter;
import com.reforms.ann.TargetQuery;

/**
 * Контракт на получение списка клиентов
 * @author evgenie
 */
public interface IClientDao {

    @TargetQuery(
            type = ST_SELECT,
            query =
            "SELECT cl.id, " +
            "       cl.name, " +
            "       addr.id AS address_id, " +
            "       addr.city, " +
            "       addr.street, " +
            "       cl.act_time AS t# " +
            "  FROM client AS cl, " +
            "         address AS addr" +
            "  WHERE cl.address_id = addr.id AND " +
            "              cl.act_time >= ::t#act_time" +
            "  ORDER BY cl.id ASC",
            orm = ClientOrm.class)
    public List<ClientOrm> loadClients(Date actTime);

    @TargetQuery(
            type = ST_SELECT,
            query =
            "SELECT cl.id, " +
            "       cl.name, " +
            "       addr.id AS address_id, " +
            "       addr.city, " +
            "       addr.street, " +
            "       cl.act_time AS t# " +
            "  FROM client AS cl, " +
            "         address AS addr" +
            "  WHERE cl.address_id = addr.id AND " +
            "              cl.id = :client_id AND " +
            "              cl.act_time >= ::t#act_time " +
            "  ORDER BY cl.id ASC",
            orm = ClientOrm.class)
    public ClientOrm findClient(long clientId, Date actTime);

    final String SELECT_CLIENT_BASE_QUERY =
            "SELECT cl.id, " +
            "       cl.name, " +
            "       addr.id AS address_id, " +
            "       addr.city, " +
            "       addr.street, " +
            "       cl.act_time AS t# " +
            "  FROM client AS cl, " +
            "         address AS addr" +
            "  WHERE cl.address_id = addr.id AND " +
            "              cl.id = ::client_id AND " +
            "              cl.act_time >= :t#act_time " +
            "  ORDER BY cl.id ASC";

    @TargetQuery(
            type = ST_SELECT,
            query = SELECT_CLIENT_BASE_QUERY,
            orm = ClientOrm.class)
    public List<ClientOrm> loadClients1(@TargetFilter("act_time") Date actTime);

    @TargetQuery(
            type = ST_SELECT,
            query = SELECT_CLIENT_BASE_QUERY,
            orm = ClientOrm.class)
    public ClientOrm findClient1(long clientId, Date actTime);

    @TargetQuery(
            type = ST_SELECT,
            query = SELECT_CLIENT_BASE_QUERY,
            orm = ClientOrm.class)
    public ClientOrm findClient2(Map<String, Object> filters);

    @TargetQuery(
            type = ST_SELECT,
            query = SELECT_CLIENT_BASE_QUERY,
            orm = ClientOrm.class)
    public ClientOrm findClient2(@TargetFilter(bobj = true) ClientFilter filter);

    @TargetQuery(
            type = ST_UPDATE,
            query = "UPDATE client " +
                    "   SET name = :name " +
                    "       WHERE id = :id",
            orm = ClientOrm.class)
    public int updateClient(String name, long clientId);

    @TargetQuery(
            type = ST_UPDATE,
            query = "UPDATE address " +
                    "   SET city = :city," +
                    "       street = :street " +
                    "       WHERE id = :id",
            orm = ClientOrm.class)
    public int updateAddres(String city, String street, long addressId);

    public default int update(ClientOrm clientOrm) {
        updateAddres(clientOrm.getCity(), clientOrm.getStreet(), clientOrm.getAddressId());
        return updateClient(clientOrm.getName(), clientOrm.getId());
    }

}














