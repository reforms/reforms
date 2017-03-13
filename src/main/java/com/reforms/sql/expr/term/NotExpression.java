package com.reforms.sql.expr.term;

import com.reforms.sql.expr.viewer.SqlBuilder;

import static com.reforms.sql.parser.SqlWords.SW_NOT;

import static com.reforms.sql.expr.term.ExpressionType.ET_NOT_EXPRESSION;

/**
 *
 * @author evgenie
 */
public class NotExpression extends Expression {

    private String notWord = SW_NOT;

    private Expression primaryExpr;

    public String getNotWord() {
        return notWord;
    }

    public void setNotWord(String notWord) {
        this.notWord = notWord;
    }

    public Expression getPrimaryExpr() {
        return primaryExpr;
    }

    public void setPrimaryExpr(Expression primaryExpr) {
        this.primaryExpr = primaryExpr;
    }

    @Override
    public ExpressionType getType() {
        return ET_NOT_EXPRESSION;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(notWord);
        sqlBuilder.appendExpression(primaryExpr);
    }
}