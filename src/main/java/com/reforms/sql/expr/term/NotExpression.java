package com.reforms.sql.expr.term;

import com.reforms.sql.expr.viewer.SqlBuilder;

import static com.reforms.sql.expr.term.ExpressionType.ET_NOT_EXPRESSION;
import static com.reforms.sql.expr.term.SqlWords.SW_NOT;

/**
 *
 * @author evgenie
 */
public class NotExpression extends Expression {

    private Expression primaryExpr;

    public Expression getPrimaryExpr() {
        return primaryExpr;
    }

    public void setPrimaryExpr(Expression primaryExpr) {
        this.primaryExpr = primaryExpr;
    }

    @Override
    public ExpressionType getType() {
        return ET_NOT_EXPRESSION;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(SW_NOT);
        sqlBuilder.appendExpression(primaryExpr);
    }
}