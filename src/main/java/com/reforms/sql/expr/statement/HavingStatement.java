package com.reforms.sql.expr.statement;

import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.viewer.SqlBuilder;

import static com.reforms.sql.parser.SqlWords.SW_HAVING;

import static com.reforms.sql.expr.term.ExpressionType.ET_HAVING_STATEMENT;

public class HavingStatement extends Expression {

    private String havingWord = SW_HAVING;

    private Expression searchExpr;

    public String getHavingWord() {
        return havingWord;
    }

    public void setHavingWord(String havingWord) {
        this.havingWord = havingWord;
    }

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
        sqlBuilder.appendWord(havingWord);
        sqlBuilder.appendExpression(searchExpr);
    }
}