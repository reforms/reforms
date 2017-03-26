package com.reforms.sql.expr.query;

import static com.reforms.sql.expr.term.ExpressionType.ET_DELETE_QUERY;

import com.reforms.sql.expr.statement.DeleteStatement;
import com.reforms.sql.expr.statement.WhereStatement;
import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.viewer.SqlBuilder;

/**
 * GRAMMAR:
 * <delete statement: searched>    ::=   DELETE FROM <table name> [ WHERE <search condition> ]
 * @author evgenie
 */
public class DeleteQuery extends Expression {

    private DeleteStatement deleteStatement;

    private WhereStatement whereStatement;

    public DeleteStatement getDeleteStatement() {
        return deleteStatement;
    }

    public void setDeleteStatement(DeleteStatement deleteStatement) {
        this.deleteStatement = deleteStatement;
    }

    public WhereStatement getWhereStatement() {
        return whereStatement;
    }

    public void setWhereStatement(WhereStatement whereStatement) {
        this.whereStatement = whereStatement;
    }

    @Override
    public ExpressionType getType() {
        return ET_DELETE_QUERY;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendExpression(deleteStatement);
        sqlBuilder.appendExpression(whereStatement);
    }
}