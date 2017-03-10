package com.reforms.sql.expr.term.predicate;

import com.reforms.sql.expr.query.SelectQuery;
import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.viewer.SqlBuilder;

import static com.reforms.sql.expr.term.ExpressionType.ET_UNIQUE_PREDICATE_EXPRESSION;
import static com.reforms.sql.expr.term.SqlWords.SW_UNIQUE;

public class UniquePredicateExpression extends PredicateExpression {

    private String uniqueWord = SW_UNIQUE;

    private SelectQuery subQuery;

    public String getUniqueWord() {
        return uniqueWord;
    }

    public void setUniqueWord(String uniqueWord) {
        this.uniqueWord = uniqueWord;
    }

    public SelectQuery getSubQuery() {
        return subQuery;
    }

    public void setSubQuery(SelectQuery subQuery) {
        this.subQuery = subQuery;
    }

    @Override
    public ExpressionType getType() {
        return ET_UNIQUE_PREDICATE_EXPRESSION;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(uniqueWord);
        sqlBuilder.appendExpression(subQuery);
    }
}
