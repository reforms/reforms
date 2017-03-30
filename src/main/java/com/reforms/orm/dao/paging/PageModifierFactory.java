package com.reforms.orm.dao.paging;

import com.reforms.ann.ThreadSafe;
import com.reforms.sql.db.DbType;

import java.util.IdentityHashMap;
import java.util.Map;

import static com.reforms.orm.OrmConfigurator.getInstance;
import static com.reforms.sql.db.DbType.*;

/**
 * Фабрика на получение объекта модификации sql выражения с целью разбивки его на постраничную загрузку.
 * @author evgenie
 */
@ThreadSafe
public class PageModifierFactory {

    private Map<DbType, IPageModifier> pageModifiers;

    public PageModifierFactory() {
        pageModifiers = new IdentityHashMap<>();
        pageModifiers.put(DBT_MIX, getInstance(PostreSqlPageModifier.class));
        pageModifiers.put(DBT_POSTGRESQL, getInstance(PostreSqlPageModifier.class));
        pageModifiers.put(DBT_ORACLE, getInstance(OraclePageModifier.class));
        pageModifiers.put(DBT_MSSQL_2000, getInstance(MsSql2000PageModifier.class));
        pageModifiers.put(DBT_MSSQL_2012, getInstance(MsSql2012PageModifier.class));
        pageModifiers.put(DBT_DB2, getInstance(PostreSqlPageModifier.class));
    }

    public IPageModifier getPageModifier(DbType dbType) {
        return pageModifiers.get(dbType);
    }
}