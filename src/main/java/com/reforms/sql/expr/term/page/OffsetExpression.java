package com.reforms.sql.expr.term.page;

import static com.reforms.sql.expr.term.ExpressionType.ET_OFFSET_EXPRESSION;

import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.viewer.SqlBuilder;

/**
 * Example:  OFFSET 10
 * @author evgenie
 */
public class OffsetExpression extends Expression {

    private String offsetWord;

    private Expression offsetExpr;

    public String getOffsetWord() {
        return offsetWord;
    }

    public void setOffsetWord(String offsetWord) {
        this.offsetWord = offsetWord;
    }

    public Expression getOffsetExpr() {
        return offsetExpr;
    }

    public void setOffsetExpr(Expression offsetExpr) {
        this.offsetExpr = offsetExpr;
    }

    @Override
    public ExpressionType getType() {
        return ET_OFFSET_EXPRESSION;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(offsetWord);
        sqlBuilder.appendExpression(offsetExpr);
    }
}
