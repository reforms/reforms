package com.reforms.sql.expr.statement;

import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.term.GroupingColumnReferenceExpression;
import com.reforms.sql.expr.viewer.SqlBuilder;

import java.util.ArrayList;
import java.util.List;

import static com.reforms.sql.expr.term.ExpressionType.ET_GROUP_BY_STATEMENT;
import static com.reforms.sql.expr.term.SqlWords.SW_BY;
import static com.reforms.sql.expr.term.SqlWords.SW_GROUP;

public class GroupByStatement extends Expression {

    private String groupWord = SW_GROUP;

    private String byWord = SW_BY;

    private List<GroupingColumnReferenceExpression> groupByExprs = new ArrayList<>();

    public String getGroupWord() {
        return groupWord;
    }

    public void setGroupWord(String groupWord) {
        this.groupWord = groupWord;
    }

    public String getByWord() {
        return byWord;
    }

    public void setByWord(String byWord) {
        this.byWord = byWord;
    }

    public List<GroupingColumnReferenceExpression> getGroupByExprs() {
        return groupByExprs;
    }

    public boolean addGroupByExpr(GroupingColumnReferenceExpression groupByExpr) {
        return groupByExprs.add(groupByExpr);
    }

    public void setGroupByExprs(List<GroupingColumnReferenceExpression> groupByExprs) {
        this.groupByExprs = groupByExprs;
    }

    @Override
    public ExpressionType getType() {
        return ET_GROUP_BY_STATEMENT;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(groupWord);
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(byWord);
        sqlBuilder.appendExpressions(groupByExprs, ",");
    }
}
