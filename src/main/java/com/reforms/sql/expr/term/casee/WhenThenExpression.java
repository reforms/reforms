package com.reforms.sql.expr.term.casee;

import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.viewer.SqlBuilder;

import static com.reforms.sql.expr.term.ExpressionType.ET_WHEN_THEN_EXPRESSION;
import static com.reforms.sql.expr.term.SqlWords.SW_THEN;
import static com.reforms.sql.expr.term.SqlWords.SW_WHEN;

public class WhenThenExpression extends Expression {

    private Expression whenExpr;

    private Expression thenExpr;

    public Expression getWhenExpr() {
        return whenExpr;
    }

    public void setWhenExpr(Expression whenExpr) {
        this.whenExpr = whenExpr;
    }

    public Expression getThenExpr() {
        return thenExpr;
    }

    public void setThenExpr(Expression thenExpr) {
        this.thenExpr = thenExpr;
    }

    @Override
    public ExpressionType getType() {
        return ET_WHEN_THEN_EXPRESSION;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(SW_WHEN);
        sqlBuilder.appendExpression(whenExpr);
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(SW_THEN);
        sqlBuilder.appendExpression(thenExpr);
    }
}
