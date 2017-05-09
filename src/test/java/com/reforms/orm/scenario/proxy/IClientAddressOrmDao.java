package com.reforms.orm.scenario.proxy;

import static com.reforms.ann.TargetQuery.*;

import com.reforms.ann.TargetFilter;
import com.reforms.ann.TargetQuery;

/**
 * Контракт на работу с адресом
 * @author evgenie
 */
public interface IClientAddressOrmDao {

    @TargetQuery(
            type = ST_UPDATE,
            query = "UPDATE address " +
                    "   SET city = :city," +
                    "       street = :street " +
                    "       WHERE id = :id")
    public int updateAddresOrm(String city, String street, long addressId);

    @TargetQuery(
            type = ST_DELETE,
            query = "DELETE FROM client WHERE id = :id")
    public int deleteAddressOrm(long clientId);

    @TargetQuery(
            type = ST_INSERT,
            query = "INSERT INTO address (id, city, street) VALUES (:address_id, :city, :street)")
    public void instertAddressOrm(@TargetFilter ClientOrm client);

}














