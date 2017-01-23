package com.reforms.sql.expr.term;

import static com.reforms.sql.expr.term.ExpressionType.ET_ALIAS_EXPRESSION;

import com.reforms.sql.expr.viewer.SqlBuilder;

/**
 *
 * @author palihov
 */
public class AliasExpression extends SelectableExpression {

    private Expression primaryExpr;

    private AsClauseExpression asClauseExpr;

    public Expression getPrimaryExpr() {
        return primaryExpr;
    }

    public void setPrimaryExpr(Expression primaryExpr) {
        this.primaryExpr = primaryExpr;
    }

    public AsClauseExpression getAsClauseExpr() {
        return asClauseExpr;
    }

    public void setAsClauseExpr(AsClauseExpression asClauseExpr) {
        this.asClauseExpr = asClauseExpr;
    }

    @Override
    public ExpressionType getType() {
        return ET_ALIAS_EXPRESSION;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendExpression(primaryExpr);
        sqlBuilder.appendExpression(asClauseExpr);
    }
}
