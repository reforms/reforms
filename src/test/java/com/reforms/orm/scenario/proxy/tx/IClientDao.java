package com.reforms.orm.scenario.proxy.tx;

import com.reforms.ann.TargetFilter;
import com.reforms.ann.TargetQuery;

public interface IClientDao {

    @TargetQuery("INSERT INTO client_1 (id, name) VALUES(:id, :name)")
    void insertClient1(@TargetFilter Client client);

    @TargetQuery("INSERT INTO client_2 (id, name) VALUES(:id, :name)")
    void insertClient2(@TargetFilter Client client);

    // client_3 is not exists!
    @TargetQuery("INSERT INTO client_3 (id, name) VALUES(:id, :name)")
    void insertClient3(@TargetFilter Client client);

    @TargetQuery("SELECT id, name FROM client_1")
    Client loadClient1(long id);

    @TargetQuery("SELECT id, name FROM client_2")
    Client loadClient2(long id);

}
