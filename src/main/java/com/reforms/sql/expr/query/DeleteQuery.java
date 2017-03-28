package com.reforms.sql.expr.query;

import com.reforms.sql.expr.statement.*;
import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.term.page.LimitExpression;
import com.reforms.sql.expr.viewer.SqlBuilder;

import static com.reforms.sql.expr.term.ExpressionType.ET_DELETE_QUERY;

/**
 * GRAMMAR:
 * <delete statement: searched>    ::=   DELETE FROM <table name> [ WHERE <search condition> ]
 * @author evgenie
 */
public class DeleteQuery extends Expression {

    private DeleteStatement deleteStatement;

    private FromStatement fromStatement;

    private UsingStatement usingStatement;

    private WhereStatement whereStatement;

    private OrderByStatement orderByStatement;

    private LimitExpression limitExpr;

    public DeleteStatement getDeleteStatement() {
        return deleteStatement;
    }

    public void setDeleteStatement(DeleteStatement deleteStatement) {
        this.deleteStatement = deleteStatement;
    }

    public FromStatement getFromStatement() {
        return fromStatement;
    }

    public void setFromStatement(FromStatement fromStatement) {
        this.fromStatement = fromStatement;
    }

    public UsingStatement getUsingStatement() {
        return usingStatement;
    }

    public void setUsingStatement(UsingStatement usingStatement) {
        this.usingStatement = usingStatement;
    }

    public WhereStatement getWhereStatement() {
        return whereStatement;
    }

    public void setWhereStatement(WhereStatement whereStatement) {
        this.whereStatement = whereStatement;
    }

    public OrderByStatement getOrderByStatement() {
        return orderByStatement;
    }

    public void setOrderByStatement(OrderByStatement orderByStatement) {
        this.orderByStatement = orderByStatement;
    }

    public LimitExpression getLimitExpr() {
        return limitExpr;
    }

    public void setLimitExpr(LimitExpression limitExpr) {
        this.limitExpr = limitExpr;
    }

    @Override
    public ExpressionType getType() {
        return ET_DELETE_QUERY;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendExpression(deleteStatement);
        sqlBuilder.appendExpression(fromStatement);
        sqlBuilder.appendExpression(usingStatement);
        sqlBuilder.appendExpression(whereStatement);
        sqlBuilder.appendExpression(orderByStatement);
        sqlBuilder.appendExpression(limitExpr);
    }
}