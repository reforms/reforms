package com.reforms.sql.expr.term.predicate;

import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.viewer.SqlBuilder;

import static com.reforms.sql.expr.term.ExpressionType.ET_NULLABLE_PREDICATE_EXPRESSION;
import static com.reforms.sql.expr.term.SqlWords.SW_IS;
import static com.reforms.sql.expr.term.SqlWords.SW_NULL;

public class NullablePredicateExpression extends PredicateExpression {

    private String isWord = SW_IS;

    private Expression expression;

    private String notWord;

    private String nullWord = SW_NULL;

    public String getIsWord() {
        return isWord;
    }

    public void setIsWord(String isWord) {
        this.isWord = isWord;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public String getNotWord() {
        return notWord;
    }

    public void setNotWord(String notWord) {
        this.notWord = notWord;
    }

    public String getNullWord() {
        return nullWord;
    }

    public void setNullWord(String nullWord) {
        this.nullWord = nullWord;
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
        sqlBuilder.appendWord(isWord);
        if (notWord != null) {
            sqlBuilder.appendSpace();
            sqlBuilder.appendWord(notWord);
        }
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(nullWord);
    }
}
