package com.reforms.sql.expr.query;

import static com.reforms.sql.expr.term.ExpressionType.ET_INSERT_QUERY;

import com.reforms.sql.expr.statement.InsertStatement;
import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.viewer.SqlBuilder;

/**
 * GRAMMAR:
 * insert query = <insert statement>    ::=   INSERT INTO <table name> <insert columns and source>
 * @author evgenie
 */
public class InsertQuery extends Expression {

    private InsertStatement insertStatement;

    public InsertStatement getInsertStatement() {
        return insertStatement;
    }

    public void setInsertStatement(InsertStatement insertStatement) {
        this.insertStatement = insertStatement;
    }

    @Override
    public ExpressionType getType() {
        return ET_INSERT_QUERY;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendExpression(insertStatement);
    }
}