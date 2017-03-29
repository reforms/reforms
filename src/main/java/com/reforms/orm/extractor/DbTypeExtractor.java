package com.reforms.orm.extractor;

import com.reforms.ann.ThreadSafe;
import com.reforms.orm.scheme.ISchemeManager;
import com.reforms.sql.db.DbType;
import com.reforms.sql.expr.query.SelectQuery;
import com.reforms.sql.expr.term.from.TableExpression;

import static com.reforms.orm.OrmConfigurator.getInstance;

/**
 * Получить тип БД
 * TODO: оптимизация
 * @author evgenie
 */
@ThreadSafe
public class DbTypeExtractor {

    public DbTypeExtractor() {
    }

    public DbType extractDbType(SelectQuery selectQuery) {
        ISchemeManager schemeManager = getInstance(ISchemeManager.class);
        if (schemeManager.getDefaultDbType() != null && schemeManager.isSingleDbType()) {
            return schemeManager.getDefaultDbType();
        }
        return extractDbType(selectQuery, schemeManager);
    }

    private DbType extractDbType(SelectQuery selectQuery, ISchemeManager schemeManager) {
        TableExpressionExtractor tableExprExtractor = new TableExpressionExtractor();
        for (TableExpression tableExpr : tableExprExtractor.extractTableExpressions(selectQuery)) {
            if (tableExpr.hasSchemeName()) {
                String schemeKey = tableExpr.getSchemeName();
                DbType dbType = schemeManager.getDbType(schemeKey);
                return dbType != null ? dbType : DbType.DBT_MIX;
            }
        }
        if (schemeManager.getDefaultSchemeName() != null) {
            return schemeManager.getDefaultDbType();
        }
        return DbType.DBT_MIX;
    }

}
