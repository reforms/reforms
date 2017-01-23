package com.reforms.sql.expr.statement;

import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.viewer.SqlBuilder;

import static com.reforms.sql.expr.term.ExpressionType.ET_WHERE_STATEMENT;
import static com.reforms.sql.expr.term.SqlWords.SW_WHERE;

public class WhereStatement extends Expression {

    private Expression searchExpr;

    public Expression getSearchExpr() {
        return searchExpr;
    }

    public void setSearchExpr(Expression searchExpr) {
        this.searchExpr = searchExpr;
    }

    @Override
    public ExpressionType getType() {
        return ET_WHERE_STATEMENT;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(SW_WHERE);
        sqlBuilder.appendExpression(searchExpr);
    }
}