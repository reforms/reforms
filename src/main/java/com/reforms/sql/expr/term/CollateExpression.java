package com.reforms.sql.expr.term;

import com.reforms.sql.expr.viewer.SqlBuilder;

import static com.reforms.sql.expr.term.ExpressionType.ET_COLLATE_EXPRESSION;
import static com.reforms.sql.expr.term.SqlWords.*;

public class CollateExpression extends Expression {

    private Expression collationNameExpr;

    public Expression getCollationNameExpr() {
        return collationNameExpr;
    }

    public void setCollationNameExpr(Expression collationNameExpr) {
        this.collationNameExpr = collationNameExpr;
    }

    @Override
    public ExpressionType getType() {
        return ET_COLLATE_EXPRESSION;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(SW_COLLATE);
        sqlBuilder.appendExpression(collationNameExpr);
    }
}
