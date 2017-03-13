package com.reforms.sql.expr.term.casee;

import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.viewer.SqlBuilder;

import static com.reforms.sql.parser.SqlWords.SW_THEN;
import static com.reforms.sql.parser.SqlWords.SW_WHEN;

import static com.reforms.sql.expr.term.ExpressionType.ET_WHEN_THEN_EXPRESSION;

/**
 * WHEN ... THEN ...
 * @author evgenie
 */
public class WhenThenExpression extends Expression {

    /** WHEN */
    private String whenWord = SW_WHEN;

    private Expression whenExpr;

    /** THEN */
    private String thenWord = SW_THEN;

    private Expression thenExpr;

    public String getWhenWord() {
        return whenWord;
    }

    public void setWhenWord(String whenWord) {
        this.whenWord = whenWord;
    }


    public Expression getWhenExpr() {
        return whenExpr;
    }

    public void setWhenExpr(Expression whenExpr) {
        this.whenExpr = whenExpr;
    }

    public String getThenWord() {
        return thenWord;
    }

    public void setThenWord(String thenWord) {
        this.thenWord = thenWord;
    }

    public Expression getThenExpr() {
        return thenExpr;
    }

    public void setThenExpr(Expression thenExpr) {
        this.thenExpr = thenExpr;
    }

    @Override
    public ExpressionType getType() {
        return ET_WHEN_THEN_EXPRESSION;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(whenWord);
        sqlBuilder.appendExpression(whenExpr);
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(thenWord);
        sqlBuilder.appendExpression(thenExpr);
    }
}
