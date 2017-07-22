package com.reforms.orm.scenario.proxy.store_procedure;

import com.reforms.ann.TargetQuery;

import java.util.List;

public interface IStoreProcedureDao {

    @TargetQuery("{? = call GET_NEXT_ID()}")
    int getNextId();

    @TargetQuery("{? = call GET_OBJECT(:name)}")
    long getObject(String name);

    /**
     * -10 = OracleTypes.CURSOR;
     * 1111 = postgresql <-> Types.Other
     * @return сдшуте
     */
    @TargetQuery(query = "{(id, name) = call LOAD_CLIENT()}", returnType = -10)
    ClientOrm getClient();

    @TargetQuery(query = "{(id, name) = call LOAD_CLIENTS()}", returnType = -10, orm = ClientOrm.class)
    List<ClientOrm> getClients();

}
