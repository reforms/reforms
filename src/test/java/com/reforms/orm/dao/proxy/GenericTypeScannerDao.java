package com.reforms.orm.dao.proxy;

import java.util.List;

public interface GenericTypeScannerDao {

    public List<String> loadNames();

    public List<ClientOrm> loadClients();

    public List<String> loadClients(String id);
}
