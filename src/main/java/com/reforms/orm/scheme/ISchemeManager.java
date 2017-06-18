package com.reforms.orm.scheme;

import com.reforms.ann.TargetApi;
import com.reforms.sql.db.DbType;

/**
 * Контракт на получение некоторых параметров БД
 * @author evgenie
 */
@TargetApi
public interface ISchemeManager {

    public String getSchemeName(String schemeKey);

    public String getDefaultSchemeName();

    public DbType getDbType(String schemeKey);

    public DbType getDefaultDbType();

    public boolean isSingleDbType();
}
