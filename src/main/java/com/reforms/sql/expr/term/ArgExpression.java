package com.reforms.sql.expr.term;

import com.reforms.sql.expr.viewer.SqlBuilder;

import static com.reforms.sql.expr.term.ExpressionType.ET_ARG_EXPRESSION;

public class ArgExpression extends SelectableExpression {

    private String specialWord;

    private SelectableExpression argValueExpr;

    public String getSpecialWord() {
        return specialWord;
    }

    public void setSpecialWord(String specialWord) {
        this.specialWord = specialWord;
    }

    public SelectableExpression getArgValueExpr() {
        return argValueExpr;
    }

    public void setArgValueExpr(SelectableExpression argValueExpr) {
        this.argValueExpr = argValueExpr;
    }

    @Override
    public ExpressionType getType() {
        return ET_ARG_EXPRESSION;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        if (specialWord != null) {
            sqlBuilder.appendSpace();
            sqlBuilder.appendWord(specialWord);
        }
        sqlBuilder.appendExpression(argValueExpr);
    }
}
