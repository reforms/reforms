package com.reforms.orm.extractor;

import static com.reforms.orm.OrmConfigurator.getInstance;

import com.reforms.ann.ThreadSafe;
import com.reforms.orm.scheme.ISchemeManager;
import com.reforms.sql.db.DbType;
import com.reforms.sql.expr.query.SelectQuery;
import com.reforms.sql.expr.term.from.TableExpression;

/**
 * Получить тип БД
 * TODO: оптимизация
 * @author evgenie
 */
@ThreadSafe
public class DbTypeExtractor {

    public DbTypeExtractor() {
    }

    private volatile DbType cachedDbType = null;

    public DbType extractDbType(SelectQuery selectQuery) {
        if (cachedDbType != null) {
            return cachedDbType;
        }
        ISchemeManager schemeManager = getInstance(ISchemeManager.class);
        DbType dbType = extractDbType(selectQuery, schemeManager);
        if (schemeManager.isSingleDbType()) {
            cachedDbType = dbType;
        }
        return dbType;
    }

    private DbType extractDbType(SelectQuery selectQuery, ISchemeManager schemeManager) {
        TableExpressionExtractor tableExprExtractor = new TableExpressionExtractor();
        for (TableExpression tableExpr : tableExprExtractor.extractTableExpressions(selectQuery)) {
            if (tableExpr.hasSchemeName()) {
                String schemeKey = tableExpr.getSchemeName();
                DbType dbType = schemeManager.getDbType(schemeKey);
                return dbType != null ? dbType : DbType.MIX;
            }
        }
        if (schemeManager.getDefaultSchemeName() != null) {
            return schemeManager.getDefaultDbType();
        }
        return DbType.MIX;
    }

}
