package com.reforms.orm;

import com.reforms.ann.TargetApi;

import java.sql.Connection;

@FunctionalInterface
@TargetApi
public interface IConnectionHolder {

    public Connection getConnection(Object connectionHolder) throws Exception;
}
