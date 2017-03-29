package com.reforms.sql.expr.query;

import com.reforms.sql.expr.statement.*;
import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.term.SelectableExpression;
import com.reforms.sql.expr.viewer.SqlBuilder;

import java.util.ArrayList;
import java.util.List;

import static com.reforms.sql.expr.term.ExpressionType.ET_SELECT_QUERY;

/**
 * TODO: ссылки.
 * https://www.postgresql.org/docs/9.0/static/sql-select.html
 * http://savage.net.au/SQL/sql-92.bnf.html
 * https://dev.mysql.com/doc/refman/5.6/en/select.html
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

    private PageStatement pageStatement;

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

    public LinkingSelectQuery getLinkingQueryAt(int index) {
        if (linkingQueries.size() > index) {
            return linkingQueries.get(index);
        }
        return null;
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

    public PageStatement getPageStatement() {
        return pageStatement;
    }

    public void setPageStatement(PageStatement pageStatement) {
        this.pageStatement = pageStatement;
    }

    @Override
    public ExpressionType getType() {
        return ET_SELECT_QUERY;
    }

    public static SelectQuery softCopyFrom(SelectQuery prototype) {
        SelectQuery selectQuery = new SelectQuery();
        softCopyTo(selectQuery, prototype);
        return selectQuery;
    }

    public static void softCopyTo(SelectQuery targetSelectQuery, SelectQuery sourceSelectQuery) {
        targetSelectQuery.setSelectStatement(sourceSelectQuery.selectStatement);
        targetSelectQuery.setFromStatement(sourceSelectQuery.fromStatement);
        targetSelectQuery.setWhereStatement(sourceSelectQuery.whereStatement);
        targetSelectQuery.setGroupByStatement(sourceSelectQuery.groupByStatement);
        targetSelectQuery.setHavingStatement(sourceSelectQuery.havingStatement);
        targetSelectQuery.setLinkingQueries(sourceSelectQuery.linkingQueries);
        targetSelectQuery.setOrderByStatement(sourceSelectQuery.orderByStatement);
        targetSelectQuery.setPageStatement(sourceSelectQuery.pageStatement);
        targetSelectQuery.setWrapped(sourceSelectQuery.isWrapped());
        targetSelectQuery.setSpacable(sourceSelectQuery.isSpacable());
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
        sqlBuilder.appendExpression(pageStatement);
    }
}