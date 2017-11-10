package com.reforms.orm.scenario.proxy.insert;

import com.reforms.ann.TargetFilter;
import com.reforms.ann.TargetQuery;

import java.math.BigDecimal;
import java.math.BigInteger;

public interface IClientDao {

    @TargetQuery(query = "INSERT INTO clients (name) VALUES(:name)")
    int insertClientAndGetIntId(@TargetFilter ClientOrm client);

    @TargetQuery("INSERT INTO clients (name) VALUES(:name)")
    long insertClientAndGetLongId(@TargetFilter ClientOrm client);

    @TargetQuery("INSERT INTO clients (name) VALUES(:name) RETURNING id AS l#")
    long insertClientAndGetLongIdWithReturningStatement(@TargetFilter ClientOrm client);

    @TargetQuery("INSERT INTO clients (name) VALUES(:name)")
    BigInteger insertClientAndGetBigIntegerId(@TargetFilter ClientOrm client);

    @TargetQuery("INSERT INTO clients (name) VALUES(:name)")
    BigDecimal insertClientAndGetBigDecimalId(@TargetFilter ClientOrm client);

    @TargetQuery("INSERT INTO clients (name) VALUES(:name)")
    boolean insertClientAndGetOkState(@TargetFilter ClientOrm client);

}
