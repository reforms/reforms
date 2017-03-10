package com.reforms.sql.expr.term;

import com.reforms.sql.expr.viewer.SqlBuilder;

import static com.reforms.sql.expr.term.ExpressionType.ET_CAST_EXPRESSION;
import static com.reforms.sql.expr.term.SqlWords.SW_AS;
import static com.reforms.sql.expr.term.SqlWords.SW_CAST;

public class CastExpression extends SelectableExpression {

    private String castWord = SW_CAST;

    private Expression operandExpr;

    private String asWord = SW_AS;

    private Expression targetExpr;

    public String getCastWord() {
        return castWord;
    }

    public void setCastWord(String castWord) {
        this.castWord = castWord;
    }

    public Expression getOperandExpr() {
        return operandExpr;
    }

    public void setOperandExpr(Expression operandExpr) {
        this.operandExpr = operandExpr;
    }

    public String getAsWord() {
        return asWord;
    }

    public void setAsWord(String asWord) {
        this.asWord = asWord;
    }

    public Expression getTargetExpr() {
        return targetExpr;
    }

    public void setTargetExpr(Expression targetExpr) {
        this.targetExpr = targetExpr;
    }

    @Override
    public ExpressionType getType() {
        return ET_CAST_EXPRESSION;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendWord(castWord);
        sqlBuilder.append("(");
        sqlBuilder.appendExpression(operandExpr);
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(asWord);
        sqlBuilder.appendExpression(targetExpr);
        sqlBuilder.append(")");
    }
}
