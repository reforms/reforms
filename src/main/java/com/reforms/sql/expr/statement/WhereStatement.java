package com.reforms.sql.expr.statement;

import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.viewer.SqlBuilder;

import static com.reforms.sql.expr.term.ExpressionType.ET_WHERE_STATEMENT;
import static com.reforms.sql.expr.term.SqlWords.SW_WHERE;

public class WhereStatement extends Expression {

    private String whereWord = SW_WHERE;

    private Expression searchExpr;

    public String getWhereWord() {
        return whereWord;
    }

    public void setWhereWord(String whereWord) {
        this.whereWord = whereWord;
    }

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
        sqlBuilder.appendWord(whereWord);
        sqlBuilder.appendExpression(searchExpr);
    }
}