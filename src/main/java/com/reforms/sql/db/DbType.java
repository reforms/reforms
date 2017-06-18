package com.reforms.sql.db;

import com.reforms.ann.TargetApi;

@TargetApi
public enum DbType {

    DBT_POSTGRESQL,
    DBT_MSSQL_2000,
    DBT_MSSQL_2012,
    DBT_ORACLE,
    DBT_DB2,
    DBT_MIX
}
