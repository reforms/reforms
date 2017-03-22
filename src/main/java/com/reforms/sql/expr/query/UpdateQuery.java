package com.reforms.sql.expr.query;

import com.reforms.sql.expr.statement.SetClauseStatement;
import com.reforms.sql.expr.statement.UpdateStatement;
import com.reforms.sql.expr.statement.WhereStatement;
import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.viewer.SqlBuilder;

import static com.reforms.sql.expr.term.ExpressionType.ET_UPDATE_QUERY;

/**
 * GRAMMAR:
 * <update statement: searched> ::=
         UPDATE <table name> SET <set clause list> [ WHERE <search condition> ]
 * @author evgenie
 */
public class UpdateQuery extends Expression {

    private UpdateStatement updateStatement;

    private SetClauseStatement setClauseStatement;

    private WhereStatement whereStatement;

    public UpdateStatement getUpdateStatement() {
        return updateStatement;
    }

    public void setUpdateStatement(UpdateStatement updateStatement) {
        this.updateStatement = updateStatement;
    }

    public SetClauseStatement getSetClauseStatement() {
        return setClauseStatement;
    }

    public void setSetClauseStatement(SetClauseStatement setClauseStatement) {
        this.setClauseStatement = setClauseStatement;
    }

    public WhereStatement getWhereStatement() {
        return whereStatement;
    }

    public void setWhereStatement(WhereStatement whereStatement) {
        this.whereStatement = whereStatement;
    }

    @Override
    public ExpressionType getType() {
        return ET_UPDATE_QUERY;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendExpression(updateStatement);
        sqlBuilder.appendExpression(setClauseStatement);
        sqlBuilder.appendExpression(whereStatement);
    }
}