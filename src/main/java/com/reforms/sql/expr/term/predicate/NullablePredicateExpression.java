package com.reforms.sql.expr.term.predicate;

import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.viewer.SqlBuilder;

import static com.reforms.sql.expr.term.ExpressionType.ET_NULLABLE_PREDICATE_EXPRESSION;
import static com.reforms.sql.expr.term.SqlWords.*;

public class NullablePredicateExpression extends PredicateExpression {

    private Expression expression;

    private boolean useNotWord;

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public boolean isUseNotWord() {
        return useNotWord;
    }

    public void setUseNotWord(boolean useNotWord) {
        this.useNotWord = useNotWord;
    }

    @Override
    public ExpressionType getType() {
        return ET_NULLABLE_PREDICATE_EXPRESSION;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendSpace();
        sqlBuilder.appendExpression(expression);
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(SW_IS);
        if (useNotWord) {
            sqlBuilder.appendSpace();
            sqlBuilder.appendWord(SW_NOT);
        }
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(SW_NULL);
    }
}
