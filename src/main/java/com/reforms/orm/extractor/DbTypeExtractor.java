package com.reforms.orm.extractor;

import com.reforms.ann.ThreadSafe;
import com.reforms.orm.scheme.ISchemeManager;
import com.reforms.sql.db.DbType;
import com.reforms.sql.expr.query.SelectQuery;
import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.term.FuncExpression;
import com.reforms.sql.expr.term.from.TableExpression;

import static com.reforms.orm.OrmConfigurator.getInstance;
import static com.reforms.sql.expr.term.ExpressionType.ET_FUNC_EXPRESSION;
import static com.reforms.sql.expr.term.ExpressionType.ET_TABLE_EXPRESSION;

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
        ExpressionScanner scanner = new ExpressionScanner();
        DbTypeFinder dbFinder = new DbTypeFinder(schemeManager);
        scanner.scan(selectQuery, dbFinder);
        return dbFinder.getDbType();
    }

    private static class DbTypeFinder implements IExpressionProcessor {

        private final ISchemeManager schemeManager;

        private DbType dbType;

        DbTypeFinder(ISchemeManager schemeManager) {
            this.schemeManager = schemeManager;
        }

        public DbType getDbType() {
            return dbType == null ? DbType.DBT_MIX : dbType;
        }

        @Override
        public boolean accept(ExpressionType exprType) {
            return ET_TABLE_EXPRESSION == exprType || ET_FUNC_EXPRESSION == exprType;
        }


        @Override
        public boolean process(ExpressionType exprType, Expression expr) {
            if (ET_TABLE_EXPRESSION == exprType) {
                TableExpression tableExpr = (TableExpression) expr;
                if (tableExpr.hasSchemeName()) {
                    String schemeKey = tableExpr.getSchemeName();
                    dbType = schemeManager.getDbType(schemeKey);
                    if (dbType == null) {
                        dbType = DbType.DBT_MIX;
                    }
                }
            }
            if (ET_FUNC_EXPRESSION == exprType) {
                FuncExpression funcExpr = (FuncExpression) expr;
                if (funcExpr.hasSchemeName()) {
                    String schemeKey = funcExpr.getSchemeName();
                    dbType = schemeManager.getDbType(schemeKey);
                    if (dbType == null) {
                        dbType = DbType.DBT_MIX;
                    }
                }
            }
            return dbType == null;
        }
    }
}
