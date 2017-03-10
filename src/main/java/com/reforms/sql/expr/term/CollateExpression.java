package com.reforms.sql.expr.term;

import com.reforms.sql.expr.viewer.SqlBuilder;

import static com.reforms.sql.expr.term.ExpressionType.ET_COLLATE_EXPRESSION;
import static com.reforms.sql.expr.term.SqlWords.SW_COLLATE;

public class CollateExpression extends Expression {

    private String collateWord = SW_COLLATE;

    private Expression collationNameExpr;

    public String getCollateWord() {
        return collateWord;
    }

    public void setCollateWord(String collateWord) {
        this.collateWord = collateWord;
    }

    public Expression getCollationNameExpr() {
        return collationNameExpr;
    }

    public void setCollationNameExpr(Expression collationNameExpr) {
        this.collationNameExpr = collationNameExpr;
    }

    @Override
    public ExpressionType getType() {
        return ET_COLLATE_EXPRESSION;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(collateWord);
        sqlBuilder.appendExpression(collationNameExpr);
    }
}
