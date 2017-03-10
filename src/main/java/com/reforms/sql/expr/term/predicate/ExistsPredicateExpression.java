package com.reforms.sql.expr.term.predicate;

import com.reforms.sql.expr.query.SelectQuery;
import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.viewer.SqlBuilder;

import static com.reforms.sql.expr.term.ExpressionType.ET_EXISTS_PREDICATE_EXPRESSION;
import static com.reforms.sql.expr.term.SqlWords.SW_EXISTS;

/**
 *
 * @author evgenie
 */
public class ExistsPredicateExpression extends PredicateExpression {

    private String notWord;

    private String existsWord = SW_EXISTS;

    private SelectQuery selectQuery;

    public String getNotWord() {
        return notWord;
    }

    public void setNotWord(String notWord) {
        this.notWord = notWord;
    }

    public String getExistsWord() {
        return existsWord;
    }

    public void setExistsWord(String existsWord) {
        this.existsWord = existsWord;
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
        if (notWord != null) {
            sqlBuilder.appendSpace();
            sqlBuilder.appendWord(notWord);
        }
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(existsWord);
        sqlBuilder.appendExpression(selectQuery);
    }
}
