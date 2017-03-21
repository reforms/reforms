package com.reforms.sql.expr.term;

import com.reforms.sql.expr.viewer.SqlBuilder;

import static com.reforms.sql.expr.term.ExpressionType.ET_MATH_EXPRESSION;

public class MathExpression extends SelectableExpression {

    private Expression firstExpr;

    private MathOperator mathOperator;

    private Expression secondExpr;

    public Expression getFirstExpr() {
        return firstExpr;
    }

    public void setFirstExpr(Expression firstExpr) {
        this.firstExpr = firstExpr;
    }

    public MathOperator getMathOperator() {
        return mathOperator;
    }

    public void setMathOperator(MathOperator mathOperator) {
        this.mathOperator = mathOperator;
    }

    public Expression getSecondExpr() {
        return secondExpr;
    }

    public void setSecondExpr(Expression secondExpr) {
        this.secondExpr = secondExpr;
    }

    @Override
    public ExpressionType getType() {
        return ET_MATH_EXPRESSION;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendSpace();
        sqlBuilder.appendExpression(firstExpr);
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(mathOperator.getSign());
        sqlBuilder.appendExpression(secondExpr);
    }
}