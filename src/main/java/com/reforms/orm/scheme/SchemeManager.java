package com.reforms.orm.scheme;

import java.util.HashMap;
import java.util.Map;

import com.reforms.sql.db.DbType;

/**
 * Конфигурация некоторых параметров БД
 * @author evgenie
 */
public class SchemeManager implements ISchemeManager {

    private static final String DEFAULT_SCHEME_NAME = "__default__";

    private Map<String, String> schemes = new HashMap<>();
    private Map<String, DbType> dbTypes = new HashMap<>();

    @Override
    public String getSchemeName(String schemeKey) {
        return schemes.get(schemeKey);
    }

    public void putSchemeName(String schemeKey, String schemeName) {
        schemes.put(schemeKey, schemeName);
    }

    @Override
    public String getDefaultSchemeName() {
        return getSchemeName(DEFAULT_SCHEME_NAME);
    }

    public void setDefaultSchemeName(String schemeName) {
        putSchemeName(DEFAULT_SCHEME_NAME, schemeName);
    }

    @Override
    public DbType getDbType(String schemeKey) {
        DbType dbType = dbTypes.get(schemeKey);
        return dbType != null ? dbType : getDefaultDbType();
    }

    public void putDbType(String schemeKey, DbType dbType) {
        dbTypes.put(schemeKey, dbType);
    }

    @Override
    public DbType getDefaultDbType() {
        return getDbType(DEFAULT_SCHEME_NAME);
    }

    public void setDefaultDbType(DbType dbType) {
        putDbType(DEFAULT_SCHEME_NAME, dbType);
    }

    @Override
    public boolean isSingleDbType() {
        return dbTypes.size() < 2;
    }
}
