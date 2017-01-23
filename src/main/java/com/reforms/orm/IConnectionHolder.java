package com.reforms.orm;

import java.sql.Connection;

public interface IConnectionHolder {

    public Connection getConnection(Object connectionHolder) throws Exception;
}
