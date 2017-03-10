package com.reforms.sql.expr.term.predicate;

import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.viewer.SqlBuilder;

/**
 *
 * @author evgenie
 */
public class EscapeExpression extends Expression {

    private String escapeWord;

    private Expression escapeValueExpr;

    public String getEscapeWord() {
        return escapeWord;
    }

    public void setEscapeWord(String escapeWord) {
        this.escapeWord = escapeWord;
    }

    public Expression getEscapeValueExpr() {
        return escapeValueExpr;
    }

    public void setEscapeValueExpr(Expression escapeValueExpr) {
        this.escapeValueExpr = escapeValueExpr;
    }

    @Override
    public ExpressionType getType() {
        return ExpressionType.ET_ESCAPE_EXPRESSION;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(escapeWord);
        sqlBuilder.appendExpression(escapeValueExpr);
    }
}
