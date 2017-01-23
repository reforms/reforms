package com.reforms.sql.expr.statement;

import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.viewer.SqlBuilder;

import static com.reforms.sql.expr.term.ExpressionType.ET_HAVING_STATEMENT;
import static com.reforms.sql.expr.term.SqlWords.SW_HAVING;

public class HavingStatement extends Expression {

    private Expression searchExpr;

    public Expression getSearchExpr() {
        return searchExpr;
    }

    public void setSearchExpr(Expression searchExpr) {
        this.searchExpr = searchExpr;
    }

    @Override
    public ExpressionType getType() {
        return ET_HAVING_STATEMENT;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(SW_HAVING);
        sqlBuilder.appendExpression(searchExpr);
    }
}