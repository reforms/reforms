package com.reforms.sql.expr.term.predicate;

import com.reforms.sql.expr.query.SelectQuery;
import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.viewer.SqlBuilder;

import static com.reforms.sql.expr.term.ExpressionType.ET_EXISTS_PREDICATE_EXPRESSION;
import static com.reforms.sql.expr.term.SqlWords.SW_EXISTS;
import static com.reforms.sql.expr.term.SqlWords.SW_NOT;

/**
 *
 * @author palihov
 */
public class ExistsPredicateExpression extends PredicateExpression {

    private boolean useNotWord;

    private SelectQuery selectQuery;

    public boolean isUseNotWord() {
        return useNotWord;
    }

    public void setUseNotWord(boolean useNotWord) {
        this.useNotWord = useNotWord;
    }

    public SelectQuery getSelectQuery() {
        return selectQuery;
    }

    public void setSelectQuery(SelectQuery selectQuery) {
        this.selectQuery = selectQuery;
    }

    @Override
    public ExpressionType getType() {
        return ET_EXISTS_PREDICATE_EXPRESSION;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        if (isUseNotWord()) {
            sqlBuilder.appendSpace();
            sqlBuilder.appendWord(SW_NOT);
        }
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(SW_EXISTS);
        sqlBuilder.appendExpression(selectQuery);
    }
}
