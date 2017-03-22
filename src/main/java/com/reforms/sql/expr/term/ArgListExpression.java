package com.reforms.sql.expr.term;

import com.reforms.sql.expr.viewer.SqlBuilder;

import java.util.ArrayList;
import java.util.List;

import static com.reforms.sql.expr.term.ExpressionType.ET_ARG_LIST_EXPRESSION;

public class ArgListExpression extends Expression {

    private List<Expression> argExprs = new ArrayList<>();

    public List<Expression> getArgExprs() {
        return argExprs;
    }

    public boolean addArgExpr(Expression argExpr) {
        return argExprs.add(argExpr);
    }

    public void setArgExprs(List<Expression> argExprs) {
        this.argExprs = argExprs;
    }

    public boolean isEmpty() {
        return argExprs.isEmpty();
    }

    public int size() {
        return argExprs.size();
    }

    @Override
    public ExpressionType getType() {
        return ET_ARG_LIST_EXPRESSION;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.append("(");
        sqlBuilder.appendExpressions(argExprs, ",");
        sqlBuilder.append(")");
    }

}