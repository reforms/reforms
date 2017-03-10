package com.reforms.sql.expr.term.predicate;

import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.viewer.SqlBuilder;

import static com.reforms.sql.expr.term.ExpressionType.ET_BETWEEN_PREDICATE_EXPRESSION;
import static com.reforms.sql.expr.term.SqlWords.SW_AND;
import static com.reforms.sql.expr.term.SqlWords.SW_BETWEEN;

/**
 *
 * @author evgenie
 */
public class BetweenPredicateExpression extends PredicateExpression {

    private Expression baseExpression;

    private String notWord;

    private String betweenWord = SW_BETWEEN;

    private Expression leftExpression;

    private String andWord = SW_AND;

    private Expression rightExpression;

    public Expression getBaseExpression() {
        return baseExpression;
    }

    public void setBaseExpression(Expression baseExpression) {
        this.baseExpression = baseExpression;
    }

    public String getNotWord() {
        return notWord;
    }

    public void setNotWord(String notWord) {
        this.notWord = notWord;
    }

    public String getBetweenWord() {
        return betweenWord;
    }

    public void setBetweenWord(String betweenWord) {
        this.betweenWord = betweenWord;
    }

    public Expression getLeftExpression() {
        return leftExpression;
    }

    public void setLeftExpression(Expression leftExpression) {
        this.leftExpression = leftExpression;
    }

    public String getAndWord() {
        return andWord;
    }

    public void setAndWord(String andWord) {
        this.andWord = andWord;
    }

    public Expression getRightExpression() {
        return rightExpression;
    }

    public void setRightExpression(Expression rightExpression) {
        this.rightExpression = rightExpression;
    }

    @Override
    public ExpressionType getType() {
        return ET_BETWEEN_PREDICATE_EXPRESSION;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendSpace();
        sqlBuilder.appendExpression(baseExpression);
        if (notWord != null) {
            sqlBuilder.appendSpace();
            sqlBuilder.appendWord(notWord);
        }
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(betweenWord);
        sqlBuilder.appendExpression(leftExpression);
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(andWord);
        sqlBuilder.appendExpression(rightExpression);
    }
}
