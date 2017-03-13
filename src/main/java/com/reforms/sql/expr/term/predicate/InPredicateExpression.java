package com.reforms.sql.expr.term.predicate;

import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.viewer.SqlBuilder;

import static com.reforms.sql.parser.SqlWords.SW_IN;

import static com.reforms.sql.expr.term.ExpressionType.ET_IN_PREDICATE_EXPRESSION;

/**
 *
 * @author evgenie
 *
 */
public class InPredicateExpression extends PredicateExpression {

    private String notWord;

    private String inWord = SW_IN;

    private Expression baseExpression;

    private Expression predicateValueExpr;

    public String getNotWord() {
        return notWord;
    }

    public void setNotWord(String notWord) {
        this.notWord = notWord;
    }

    public String getInWord() {
        return inWord;
    }

    public void setInWord(String inWord) {
        this.inWord = inWord;
    }

    public Expression getBaseExpression() {
        return baseExpression;
    }

    public void setBaseExpression(Expression baseExpression) {
        this.baseExpression = baseExpression;
    }

    public Expression getPredicateValueExpr() {
        return predicateValueExpr;
    }

    public void setPredicateValueExpr(Expression predicateValueExpr) {
        this.predicateValueExpr = predicateValueExpr;
    }

    @Override
    public ExpressionType getType() {
        return ET_IN_PREDICATE_EXPRESSION;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendExpression(baseExpression);
        if (notWord != null) {
            sqlBuilder.appendSpace();
            sqlBuilder.appendWord(notWord);
        }
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(inWord);
        sqlBuilder.appendExpression(predicateValueExpr);
    }
}
