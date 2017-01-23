package com.reforms.sql.expr.term;

import static com.reforms.sql.expr.term.ExpressionType.ET_SEARCH_GROUP_EXPRESSION;

import java.util.ArrayList;
import java.util.List;

import com.reforms.sql.expr.viewer.SqlBuilder;

/**
 * Пример:
 * id = ? AND name = 'Тапочки' OR price = 100.00 ...
 *
 * 0        - leftExpr
 * 1        - conditionFlowTypeExpr
 * 2        - rightExpr
 * ...
 * N - 1    - conditionFlowTypeExpr
 * N        - rightExpr
 * @author palihov
 */
public class SearchGroupExpression extends Expression {

    private List<Expression> groupExprs = new ArrayList<>();

    public void add(Expression expr) {
        groupExprs.add(expr);
    }

    public int getExprIndex(Expression expr) {
        return groupExprs.indexOf(expr);
    }

    public boolean removeExpr(int exprIndex) {
        return groupExprs.remove(exprIndex) != null;
    }

    public Expression get(int index) {
        return groupExprs.get(index);
    }

    public int size() {
        return groupExprs.size();
    }

    @Override
    public ExpressionType getType() {
        return ET_SEARCH_GROUP_EXPRESSION;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendExpressions(groupExprs, "");
    }
}
