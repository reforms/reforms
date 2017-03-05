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

    public DbType extractDbType(SelectQuery selectQuery) {
        TableExpressionExtractor tableExprExtractor = new TableExpressionExtractor();
        ISchemeManager schemeManager = getInstance(ISchemeManager.class);
        for (TableExpression tableExpr : tableExprExtractor.extractFilterExpressions(selectQuery)) {
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
