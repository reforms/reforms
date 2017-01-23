package com.reforms.sql.expr.statement;

import java.util.ArrayList;
import java.util.List;

import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.term.SortKeyExpression;
import com.reforms.sql.expr.viewer.SqlBuilder;

import static com.reforms.sql.expr.term.ExpressionType.ET_ORDER_BY_STATEMENT;
import static com.reforms.sql.expr.term.SqlWords.*;

public class OrderByStatement extends Expression {

    private List<SortKeyExpression> sortKeyExprs = new ArrayList<>();

    public List<SortKeyExpression> getSortKeyExprs() {
        return sortKeyExprs;
    }

    public boolean addSortKeyExpr(SortKeyExpression sortKeyExpr) {
        return sortKeyExprs.add(sortKeyExpr);
    }

    public void setSortKeyExprs(List<SortKeyExpression> sortKeyExprs) {
        this.sortKeyExprs = sortKeyExprs;
    }

    @Override
    public ExpressionType getType() {
        return ET_ORDER_BY_STATEMENT;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(SW_ORDER_BY);
        sqlBuilder.appendExpressions(sortKeyExprs, ",");
    }
}