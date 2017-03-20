package com.reforms.sql.expr.query;

import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.viewer.SqlBuilder;

import static com.reforms.sql.expr.term.ExpressionType.ET_LINKING_SELECT_QUERY;

public class LinkingSelectQuery extends Expression {

    private String linkedWord;

    private String allWord;

    private SelectQuery linkedSelectQuery;

    public String getLinkedWord() {
        return linkedWord;
    }

    public void setLinkedWord(String linkedWord) {
        this.linkedWord = linkedWord;
    }

    public String getAllWord() {
        return allWord;
    }

    public void setAllWord(String allWord) {
        this.allWord = allWord;
    }

    public SelectQuery getLinkedSelectQuery() {
        return linkedSelectQuery;
    }

    public void setLinkedSelectQuery(SelectQuery linkedSelectQuery) {
        this.linkedSelectQuery = linkedSelectQuery;
    }

    @Override
    public ExpressionType getType() {
        return ET_LINKING_SELECT_QUERY;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        if (linkedWord != null) {
            sqlBuilder.appendSpace();
            sqlBuilder.appendWord(linkedWord);
        }
        if (allWord != null) {
            sqlBuilder.appendSpace();
            sqlBuilder.appendWord(allWord);
        }
        sqlBuilder.appendExpression(linkedSelectQuery);
    }
}
