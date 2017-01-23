package com.reforms.sql.expr.term.predicate;

import static com.reforms.sql.expr.term.ExpressionType.ET_COMPARISON_PREDICATE_EXPRESSION;

import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.viewer.SqlBuilder;

public class ComparisonPredicateExpression extends PredicateExpression {

    private Expression leftExpr;

    private ComparisonOperatorType compOperatorType;

    private Expression rightExpr;

    public Expression getLeftExpr() {
        return leftExpr;
    }

    public void setLeftExpr(Expression leftExpr) {
        this.leftExpr = leftExpr;
    }

    public ComparisonOperatorType getCompOperatorType() {
        return compOperatorType;
    }

    public void setCompOperatorType(ComparisonOperatorType compOperatorType) {
        this.compOperatorType = compOperatorType;
    }

    public Expression getRightExpr() {
        return rightExpr;
    }

    public void setRightExpr(Expression rightExpr) {
        this.rightExpr = rightExpr;
    }

    @Override
    public ExpressionType getType() {
        return ET_COMPARISON_PREDICATE_EXPRESSION;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendSpace();
        sqlBuilder.appendExpression(leftExpr);
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(compOperatorType.getOperator());
        sqlBuilder.appendExpression(rightExpr);
    }
}