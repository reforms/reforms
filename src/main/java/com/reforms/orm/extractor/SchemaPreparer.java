package com.reforms.orm.extractor;

import com.reforms.ann.ThreadSafe;
import com.reforms.orm.scheme.ISchemeManager;
import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.term.FuncExpression;
import com.reforms.sql.expr.term.from.TableExpression;

import static com.reforms.orm.OrmConfigurator.getInstance;
import static com.reforms.sql.expr.term.ExpressionType.ET_FUNC_EXPRESSION;
import static com.reforms.sql.expr.term.ExpressionType.ET_TABLE_EXPRESSION;

@ThreadSafe
public class SchemaPreparer implements IExpressionProcessor {

    @Override
    public boolean accept(ExpressionType exprType) {
        return ET_TABLE_EXPRESSION == exprType || ET_FUNC_EXPRESSION == exprType;
    }

    @Override
    public boolean process(ExpressionType exprType, Expression expr) {
        ISchemeManager schemeManager = getInstance(ISchemeManager.class);
        if (ET_TABLE_EXPRESSION == exprType) {
            TableExpression tableExpr = (TableExpression) expr;
            if (tableExpr.hasSchemeName()) {
                String schemeKey = tableExpr.getSchemeName();
                String originScheme = schemeManager.getSchemeName(schemeKey);
                if (originScheme != null) {
                    tableExpr.setSchemeName(originScheme);
                }
            } else if (schemeManager.getDefaultSchemeName() != null) {
                tableExpr.setSchemeName(schemeManager.getDefaultSchemeName());
            }
        } else if (ET_FUNC_EXPRESSION == exprType) {
            FuncExpression funcExpr = (FuncExpression) expr;
            if (funcExpr.hasSchemeName()) {
                String schemeKey = funcExpr.getSchemeName();
                String originScheme = schemeManager.getSchemeName(schemeKey);
                if (originScheme != null) {
                    funcExpr.setSchemeName(originScheme);
                }
            }
            // !!! Не используем дефолтную схему для функций - только явно пользователь должен указать
        }
        return true;
    }
}