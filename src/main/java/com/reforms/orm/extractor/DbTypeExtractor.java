package com.reforms.orm.extractor;

import com.reforms.orm.OrmConfigurator;
import com.reforms.orm.OrmContext;
import com.reforms.orm.scheme.ISchemeManager;
import com.reforms.sql.db.DbType;
import com.reforms.sql.expr.query.SelectQuery;
import com.reforms.sql.expr.term.from.TableExpression;

/**
 * Получить тип БД
 * TODO: оптимизация
 * @author evgenie
 */
public class DbTypeExtractor {

    public DbType extractDbType(SelectQuery selectQuery) {
        OrmContext rCtx = OrmConfigurator.get(OrmContext.class);
        TableExpressionExtractor tableExprExtractor = new TableExpressionExtractor();
        ISchemeManager schemeManager = rCtx.getSchemeManager();
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
