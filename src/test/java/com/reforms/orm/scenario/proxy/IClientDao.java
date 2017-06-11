package com.reforms.orm.scenario.proxy;

import static com.reforms.ann.TargetQuery.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.reforms.ann.TargetFilter;
import com.reforms.ann.TargetQuery;
import com.reforms.orm.dao.filter.column.ISelectedColumnFilter;

/**
 * Контракт на получение списка клиентов
 * @author evgenie
 */
public interface IClientDao {

    @TargetQuery(
            type = QT_SELECT,
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
            "              cl.act_time >= :t#act_time" +
            "  ORDER BY cl.id ASC",
            orm = ClientOrm.class)
    public List<ClientOrm> loadClients(Date actTime);

    @TargetQuery(
            type = QT_SELECT,
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
            "              cl.act_time >= :t#act_time" +
            "  ORDER BY cl.id ASC",
            orm = ClientOrm.class)
    public List<ClientOrm> loadClients(ISelectedColumnFilter columnFilter, Date actTime);

    @TargetQuery(
            type = QT_SELECT,
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
            type = QT_SELECT,
            query = SELECT_CLIENT_BASE_QUERY,
            orm = ClientOrm.class)
    public List<ClientOrm> loadClients1(@TargetFilter("act_time") Date actTime);

    @TargetQuery(
            type = QT_SELECT,
            query = SELECT_CLIENT_BASE_QUERY,
            orm = ClientOrm.class)
    public ClientOrm findClient1(long clientId, Date actTime);

    @TargetQuery(
            type = QT_SELECT,
            query = SELECT_CLIENT_BASE_QUERY,
            orm = ClientOrm.class)
    public ClientOrm findClient2(Map<String, Object> filters);

    @TargetQuery(
            type = QT_SELECT,
            query = SELECT_CLIENT_BASE_QUERY,
            orm = ClientOrm.class)
    public ClientOrm findClient2(@TargetFilter ClientFilter filter);

    @TargetQuery(
            type = QT_UPDATE,
            query = "UPDATE client " +
                    "   SET name = :name " +
                    "       WHERE id = :id")
    public int updateClient(String name, long clientId);

    @TargetQuery(
            type = QT_UPDATE,
            query = "UPDATE address " +
                    "   SET city = :city," +
                    "       street = :street " +
                    "       WHERE id = :id")
    public int updateAddres(String city, String street, long addressId);

    public default int update(ClientOrm clientOrm) {
        updateAddres(clientOrm.getCity(), clientOrm.getStreet(), clientOrm.getAddressId());
        return updateClient(clientOrm.getName(), clientOrm.getId());
    }

    @TargetQuery(
            type = QT_DELETE,
            query = "DELETE FROM address WHERE id = :id")
    public int deleteClient(long addressId);

    @TargetQuery(
            type = QT_DELETE,
            query = "DELETE FROM client WHERE id = :id")
    public int deleteAddress(long clientId);

    public default int delete(long addressId, long clientId) {
        deleteAddress(addressId);
        return deleteClient(clientId);
    }

    @TargetQuery(
            type = QT_INSERT,
            query = "INSERT INTO address (id, city, street) VALUES (:address_id, :city, :street)")
    public void instertAddress(@TargetFilter ClientOrm client);

    @TargetQuery(
            type = QT_INSERT,
            query = "INSERT INTO client (id, name, address_id, act_time) VALUES (:id, :name, :address_id, :t#act_time)")
    public void instertClient(@TargetFilter ClientOrm client);

    public default void instert(ClientOrm clientOrm) {
        instertAddress(clientOrm);
        instertClient(clientOrm);
    }

}














