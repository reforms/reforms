package com.reforms.sql.expr.term.casee;

import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.term.SelectableExpression;
import com.reforms.sql.expr.viewer.SqlBuilder;

import java.util.ArrayList;
import java.util.List;

import static com.reforms.sql.expr.term.ExpressionType.ET_CASE_EXPRESSION;
import static com.reforms.sql.expr.term.SqlWords.*;

/**
 *
 * @author palihov
 */
public class CaseExpression extends SelectableExpression {

    private Expression operandExpr;

    private List<Expression> whenThenExprs = new ArrayList<>();

    private Expression elseExpr;

    public Expression getOperandExpr() {
        return operandExpr;
    }

    public void setOperandExpr(Expression operandExpr) {
        this.operandExpr = operandExpr;
    }

    public List<Expression> getWhenThenExprs() {
        return whenThenExprs;
    }

    public boolean addWhenThenExprs(Expression whenThenExpr) {
        return whenThenExprs.add(whenThenExpr);
    }

    public void setWhenThenExprs(List<Expression> whenThenExprs) {
        this.whenThenExprs = whenThenExprs;
    }

    public Expression getElseExpr() {
        return elseExpr;
    }

    public boolean hasElseExpr() {
        return elseExpr != null;
    }

    public void setElseExpr(Expression elseExpr) {
        this.elseExpr = elseExpr;
    }

    @Override
    public ExpressionType getType() {
        return ET_CASE_EXPRESSION;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(SW_CASE);
        if (operandExpr != null) {
            sqlBuilder.appendSpace();
            sqlBuilder.appendExpression(operandExpr);
        }
        for (Expression whenThenExpr : whenThenExprs) {
            sqlBuilder.appendExpression(whenThenExpr);
        }
        if (elseExpr != null) {
            sqlBuilder.appendSpace();
            sqlBuilder.appendWord(SW_ELSE);
            sqlBuilder.appendExpression(elseExpr);
        }
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(SW_END);
    }
}