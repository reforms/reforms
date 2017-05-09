package com.reforms.orm.scenario.proxy;

/**
 * Контракт на получение списка клиентов
 * @author evgenie
 */
public interface IClientWithExtendsDao extends IClientOrmDao, IClientAddressOrmDao {

    public default int update(ClientOrm clientOrm) {
        updateAddresOrm(clientOrm.getCity(), clientOrm.getStreet(), clientOrm.getAddressId());
        return updateClientOrm(clientOrm.getName(), clientOrm.getId());
    }

    public default int delete(long addressId, long clientId) {
        deleteAddressOrm(addressId);
        return deleteClientOrm(clientId);
    }

    public default void instert(ClientOrm clientOrm) {
        instertAddressOrm(clientOrm);
        instertClientOrm(clientOrm);
    }

}














