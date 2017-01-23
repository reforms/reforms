package com.reforms.sql.expr.term;

import static com.reforms.sql.expr.term.ExpressionType.ET_SORT_KEY_EXPRESSION;

import com.reforms.sql.expr.viewer.SqlBuilder;

public class SortKeyExpression extends Expression {

    private Expression sortKeyValueExpr;

    private CollateExpression collateExpr;

    private String orderingSpec;

    public Expression getSortKeyValueExpr() {
        return sortKeyValueExpr;
    }

    public void setSortKeyValueExpr(Expression sortKeyValueExpr) {
        this.sortKeyValueExpr = sortKeyValueExpr;
    }

    public CollateExpression getCollateExpr() {
        return collateExpr;
    }

    public void setCollateExpr(CollateExpression collateExpr) {
        this.collateExpr = collateExpr;
    }

    public String getOrderingSpec() {
        return orderingSpec;
    }

    public void setOrderingSpec(String orderingSpec) {
        this.orderingSpec = orderingSpec;
    }

    @Override
    public ExpressionType getType() {
        return ET_SORT_KEY_EXPRESSION;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendExpression(sortKeyValueExpr);
        sqlBuilder.appendExpression(collateExpr);
        if (orderingSpec != null) {
            sqlBuilder.appendSpace();
            sqlBuilder.appendWord(orderingSpec);
        }
    }
}
