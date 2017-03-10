package com.reforms.sql.expr.term.casee;

import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.viewer.SqlBuilder;

import static com.reforms.sql.expr.term.ExpressionType.ET_ELSE_EXPRESSION;
import static com.reforms.sql.expr.term.SqlWords.SW_ELSE;

/**
 * ELSE else_result_expression
 * @author evgenie
 */
public class ElseExpression extends Expression {

    /** ELSE */
    private String elseWord = SW_ELSE;

    private Expression resultExpr;

    public String getElseWord() {
        return elseWord;
    }

    public void setElseWord(String elseWord) {
        this.elseWord = elseWord;
    }

    public Expression getResultExpr() {
        return resultExpr;
    }

    public void setResultExpr(Expression resultExpr) {
        this.resultExpr = resultExpr;
    }

    @Override
    public ExpressionType getType() {
        return ET_ELSE_EXPRESSION;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(elseWord);
        sqlBuilder.appendExpression(resultExpr);
    }
}
