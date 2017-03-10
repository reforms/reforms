package com.reforms.sql.expr.statement;

import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.term.SortKeyExpression;
import com.reforms.sql.expr.viewer.SqlBuilder;

import java.util.ArrayList;
import java.util.List;

import static com.reforms.sql.expr.term.ExpressionType.ET_ORDER_BY_STATEMENT;
import static com.reforms.sql.expr.term.SqlWords.SW_BY;
import static com.reforms.sql.expr.term.SqlWords.SW_ORDER;

public class OrderByStatement extends Expression {

    private String orderWord = SW_ORDER;

    private String byWord = SW_BY;

    private List<SortKeyExpression> sortKeyExprs = new ArrayList<>();

    public String getOrderWord() {
        return orderWord;
    }

    public void setOrderWord(String orderWord) {
        this.orderWord = orderWord;
    }

    public String getByWord() {
        return byWord;
    }

    public void setByWord(String byWord) {
        this.byWord = byWord;
    }

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
        sqlBuilder.appendWord(orderWord);
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(byWord);
        sqlBuilder.appendExpressions(sortKeyExprs, ",");
    }
}