package com.reforms.sql.expr.term.predicate;

import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.viewer.SqlBuilder;

import static com.reforms.sql.expr.term.ExpressionType.ET_BETWEEN_PREDICATE_EXPRESSION;
import static com.reforms.sql.expr.term.SqlWords.*;

/**
 *
 * @author evgenie
 */
public class BetweenPredicateExpression extends PredicateExpression {

    private boolean useNotWord;

    private Expression baseExpression;

    private Expression leftExpression;

    private Expression rightExpression;

    public boolean isUseNotWord() {
        return useNotWord;
    }

    public void setUseNotWord(boolean useNotWord) {
        this.useNotWord = useNotWord;
    }

    public Expression getBaseExpression() {
        return baseExpression;
    }

    public void setBaseExpression(Expression baseExpression) {
        this.baseExpression = baseExpression;
    }

    public Expression getLeftExpression() {
        return leftExpression;
    }

    public void setLeftExpression(Expression leftExpression) {
        this.leftExpression = leftExpression;
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
        if (isUseNotWord()) {
            sqlBuilder.appendSpace();
            sqlBuilder.appendWord(SW_NOT);
        }
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(SW_BETWEEN);
        sqlBuilder.appendExpression(leftExpression);
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(SW_AND);
        sqlBuilder.appendExpression(rightExpression);
    }
}
