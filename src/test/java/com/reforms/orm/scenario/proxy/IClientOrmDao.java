package com.reforms.orm.scenario.proxy;

import static com.reforms.ann.TargetQuery.*;

import com.reforms.ann.TargetFilter;
import com.reforms.ann.TargetQuery;

/**
 * Контракт на получение списка клиентов
 * @author evgenie
 */
public interface IClientOrmDao {

    @TargetQuery(
            type = ST_UPDATE,
            query = "UPDATE client " +
                    "   SET name = :name " +
                    "       WHERE id = :id")
    public int updateClientOrm(String name, long clientId);

    @TargetQuery(
            type = ST_DELETE,
            query = "DELETE FROM address WHERE id = :id")
    public int deleteClientOrm(long addressId);

    @TargetQuery(
            type = ST_INSERT,
            query = "INSERT INTO client (id, name, address_id, act_time) VALUES (:id, :name, :address_id, :t#act_time)")
    public void instertClientOrm(@TargetFilter ClientOrm client);

}














