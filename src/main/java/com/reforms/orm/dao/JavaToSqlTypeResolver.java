package com.reforms.orm.dao;

import com.reforms.ann.TargetApi;
import com.reforms.ann.ThreadSafe;
import com.reforms.sql.db.DbType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.reforms.sql.db.DbType.*;

/**
 * Реализация
 * @author evgenie
 */
@ThreadSafe
@TargetApi
public class JavaToSqlTypeResolver implements IJavaToSqlTypeResolver {

    private final Map<Class<?>, Integer> java2sql;

    private final Map<DbType, Integer> cursorTypes;

    public JavaToSqlTypeResolver() {
        Map<Class<?>, Integer> jTypes = new HashMap<>();
        jTypes.put(String.class, Types.VARCHAR);
        jTypes.put(BigDecimal.class, Types.NUMERIC);
        jTypes.put(boolean.class, Types.BIT);
        jTypes.put(Boolean.class, Types.BIT);
        jTypes.put(byte.class, Types.TINYINT);
        jTypes.put(short.class, Types.SMALLINT);
        jTypes.put(int.class, Types.INTEGER);
        jTypes.put(Integer.class, Types.INTEGER);
        jTypes.put(BigInteger.class, Types.BIGINT);
        jTypes.put(long.class, Types.BIGINT);
        jTypes.put(Long.class, Types.BIGINT);
        jTypes.put(float.class, Types.REAL);
        jTypes.put(Float.class, Types.REAL);
        jTypes.put(double.class, Types.DOUBLE);
        jTypes.put(Double.class, Types.DOUBLE);
        jTypes.put(byte[].class, Types.BINARY);
        jTypes.put(java.sql.Date.class, Types.DATE);
        jTypes.put(java.util.Date.class, Types.DATE);
        jTypes.put(java.sql.Time.class, Types.TIME);
        jTypes.put(java.sql.Timestamp.class, Types.TIMESTAMP);
        jTypes.put(Clob.class, Types.CLOB);
        jTypes.put(Blob.class, Types.BLOB);
        jTypes.put(Array.class, Types.ARRAY);
        jTypes.put(Struct.class, Types.STRUCT);
        jTypes.put(Ref.class, Types.REF);
        jTypes.put(Object.class, Types.JAVA_OBJECT);
        jTypes.put(String.class, Types.VARCHAR);
        jTypes.put(String.class, Types.VARCHAR);
        jTypes.put(String.class, Types.VARCHAR);
        jTypes.put(String.class, Types.VARCHAR);
        jTypes.put(String.class, Types.VARCHAR);
        jTypes.put(String.class, Types.VARCHAR);
        jTypes.put(String.class, Types.VARCHAR);
        java2sql = Collections.unmodifiableMap(jTypes);

        Map<DbType, Integer> cTypes = new HashMap<>();
        cTypes.put(DBT_H2DB, Types.REF_CURSOR);        // i don't now
        cTypes.put(DBT_DB2, Types.REF_CURSOR);         // i don't now
        cTypes.put(DBT_POSTGRESQL, Types.OTHER);       // it's ok
        cTypes.put(DBT_ORACLE, -10);                   // it's ok
        cTypes.put(DBT_MIX, Types.OTHER);              // like postgres
        cTypes.put(DBT_MSSQL_2000, Types.REF_CURSOR);  // i don't now
        cTypes.put(DBT_MSSQL_2012, Types.REF_CURSOR);  // i don't now

        cursorTypes = Collections.unmodifiableMap(cTypes);
    }

    @Override
    public Integer getReturnSqlType(Class<?> returnType) {
        return java2sql.get(returnType);
    }

    @Override
    public Integer getCursorType(DbType dbType) {
        return cursorTypes.get(dbType);
    }
}
