package com.reforms.sql.expr.term;

import com.reforms.sql.expr.viewer.SqlBuilder;

import java.util.ArrayList;
import java.util.List;

import static com.reforms.sql.expr.term.ExpressionType.ET_VALUE_LIST_EXPRESSION;

public class ValueListExpression extends Expression {

    private List<Expression> valueExprs = new ArrayList<>();

    public List<Expression> getValueExprs() {
        return valueExprs;
    }

    public boolean addValueExpr(Expression valueExpr) {
        return valueExprs.add(valueExpr);
    }

    public void setValueExprs(List<Expression> valueExprs) {
        this.valueExprs = valueExprs;
    }

    public boolean isEmpty() {
        return valueExprs.isEmpty();
    }

    public int size() {
        return valueExprs.size();
    }

    @Override
    public ExpressionType getType() {
        return ET_VALUE_LIST_EXPRESSION;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.append("(");
        sqlBuilder.appendExpressions(valueExprs, ",");
        sqlBuilder.append(")");
    }

}