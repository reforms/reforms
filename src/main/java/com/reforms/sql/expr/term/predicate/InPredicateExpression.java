package com.reforms.sql.expr.term.predicate;

import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.viewer.SqlBuilder;

import static com.reforms.sql.expr.term.ExpressionType.ET_IN_PREDICATE_EXPRESSION;
import static com.reforms.sql.expr.term.SqlWords.SW_IN;
import static com.reforms.sql.expr.term.SqlWords.SW_NOT;

/**
 *
 * @author evgenie
 *
 */
public class InPredicateExpression extends PredicateExpression {

    private Expression baseExpression;

    private boolean useNotWord;

    private Expression predicateValueExpr;

    public Expression getBaseExpression() {
        return baseExpression;
    }

    public void setBaseExpression(Expression baseExpression) {
        this.baseExpression = baseExpression;
    }

    public boolean isUseNotWord() {
        return useNotWord;
    }

    public void setUseNotWord(boolean useNotWord) {
        this.useNotWord = useNotWord;
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
        if (isUseNotWord()) {
            sqlBuilder.appendSpace();
            sqlBuilder.appendWord(SW_NOT);
        }
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(SW_IN);
        sqlBuilder.appendExpression(predicateValueExpr);
    }
}
