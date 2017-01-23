package com.reforms.sql.expr.term;

import com.reforms.sql.expr.viewer.SqlBuilder;

import static com.reforms.sql.expr.term.ExpressionType.ET_CAST_EXPRESSION;
import static com.reforms.sql.expr.term.SqlWords.SW_AS;
import static com.reforms.sql.expr.term.SqlWords.SW_CAST;

public class CastExpression extends SelectableExpression {

    private Expression operandExpr;

    private Expression targetExpr;

    public Expression getOperandExpr() {
        return operandExpr;
    }

    public void setOperandExpr(Expression operandExpr) {
        this.operandExpr = operandExpr;
    }

    public Expression getTargetExpr() {
        return targetExpr;
    }

    public void setTargetExpr(Expression targetExpr) {
        this.targetExpr = targetExpr;
    }

    @Override
    public ExpressionType getType() {
        return ET_CAST_EXPRESSION;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendWord(SW_CAST);
        sqlBuilder.append("(");
        sqlBuilder.appendExpression(operandExpr);
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(SW_AS);
        sqlBuilder.appendExpression(targetExpr);
        sqlBuilder.append(")");
    }
}
