package com.reforms.orm.scenario.proxy;


/**
 * Контракт на получение списка клиентов
 * @author evgenie
 */
public interface IClientWithDelegateDao {

    IClientAddressOrmDao getClientAddressOrmDao();

    IClientOrmDao getClientOrmDao();

    public default int update(ClientOrm clientOrm) {
        IClientAddressOrmDao addressDao = getClientAddressOrmDao();
        IClientOrmDao clientDao = getClientOrmDao();
        addressDao.updateAddresOrm(clientOrm.getCity(), clientOrm.getStreet(), clientOrm.getAddressId());
        return clientDao.updateClientOrm(clientOrm.getName(), clientOrm.getId());
    }

    public default int delete(long addressId, long clientId) {
        IClientAddressOrmDao addressDao = getClientAddressOrmDao();
        IClientOrmDao clientDao = getClientOrmDao();
        addressDao.deleteAddressOrm(addressId);
        return clientDao.deleteClientOrm(clientId);
    }

    public default void instert(ClientOrm clientOrm) {
        IClientAddressOrmDao addressDao = getClientAddressOrmDao();
        IClientOrmDao clientDao = getClientOrmDao();
        addressDao.instertAddressOrm(clientOrm);
        clientDao.instertClientOrm(clientOrm);
    }
}