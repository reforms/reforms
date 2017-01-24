package com.reforms.sql.expr.query;

import static com.reforms.sql.expr.term.ExpressionType.ET_SELECT_QUERY;

import java.util.ArrayList;
import java.util.List;

import com.reforms.sql.expr.statement.*;
import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.term.SelectableExpression;
import com.reforms.sql.expr.viewer.SqlBuilder;

/**
 * https://www.postgresql.org/docs/9.0/static/sql-select.html
 * @author evgenie
 */
public class SelectQuery extends SelectableExpression {

    private SelectStatement selectStatement;

    private FromStatement fromStatement;

    private WhereStatement whereStatement;

    private GroupByStatement groupByStatement;

    private HavingStatement havingStatement;

    private List<LinkingSelectQuery> linkingQueries = new ArrayList<>();

    private OrderByStatement orderByStatement;

    public SelectStatement getSelectStatement() {
        return selectStatement;
    }

    public void setSelectStatement(SelectStatement selectStatement) {
        this.selectStatement = selectStatement;
    }

    public FromStatement getFromStatement() {
        return fromStatement;
    }

    public void setFromStatement(FromStatement fromStatement) {
        this.fromStatement = fromStatement;
    }

    public WhereStatement getWhereStatement() {
        return whereStatement;
    }

    public void setWhereStatement(WhereStatement whereStatement) {
        this.whereStatement = whereStatement;
    }

    public GroupByStatement getGroupByStatement() {
        return groupByStatement;
    }

    public void setGroupByStatement(GroupByStatement groupByStatement) {
        this.groupByStatement = groupByStatement;
    }

    public HavingStatement getHavingStatement() {
        return havingStatement;
    }

    public void setHavingStatement(HavingStatement havingStatement) {
        this.havingStatement = havingStatement;
    }

    public List<LinkingSelectQuery> getLinkingQueries() {
        return linkingQueries;
    }

    public boolean addLinkingQuery(LinkingSelectQuery linkingQuery) {
        return linkingQueries.add(linkingQuery);
    }

    public void setLinkingQueries(List<LinkingSelectQuery> linkingQueries) {
        this.linkingQueries = linkingQueries;
    }

    public OrderByStatement getOrderByStatement() {
        return orderByStatement;
    }

    public void setOrderByStatement(OrderByStatement orderByStatement) {
        this.orderByStatement = orderByStatement;
    }

    @Override
    public ExpressionType getType() {
        return ET_SELECT_QUERY;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendExpression(selectStatement);
        sqlBuilder.appendExpression(fromStatement);
        sqlBuilder.appendExpression(whereStatement);
        sqlBuilder.appendExpression(groupByStatement);
        sqlBuilder.appendExpression(havingStatement);
        sqlBuilder.appendExpressions(linkingQueries, "");
        sqlBuilder.appendExpression(orderByStatement);
    }
}