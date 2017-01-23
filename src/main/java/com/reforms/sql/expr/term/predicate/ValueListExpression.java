package com.reforms.sql.expr.term.predicate;

import static com.reforms.sql.expr.term.ExpressionType.ET_VALUE_LIST_EXPRESSION;

import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.viewer.SqlBuilder;

import java.util.ArrayList;
import java.util.List;

public class ValueListExpression extends Expression {

    private List<Expression> valueExprs = new ArrayList<>();

    public List<Expression> getValueExprs() {
        return valueExprs;
    }

    public boolean addValueExpr(Expression arg) {
        return valueExprs.add(arg);
    }

    public void setValueExprs(List<Expression> valueExprs) {
        this.valueExprs = valueExprs;
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
        sqlBuilder.appendSpace();
        sqlBuilder.append("(");
        sqlBuilder.appendExpressions(valueExprs, ",");
        sqlBuilder.append(")");
    }

}