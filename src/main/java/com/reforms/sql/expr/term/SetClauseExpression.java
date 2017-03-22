package com.reforms.sql.expr.term;

import com.reforms.sql.expr.viewer.SqlBuilder;

import static com.reforms.sql.expr.term.ExpressionType.ET_SET_CLAUSE_EXPRESSION;
import static com.reforms.sql.expr.term.predicate.ComparisonOperator.COT_EQUALS;

/**
 * columnName1 = value1
 * GRAMMAR:
 * <set clause>    ::=   <object column> <equals operator> <update source>
 * @author evgenie
 */
public class SetClauseExpression extends Expression {

    private ColumnExpression columnExpr;

    private Expression updateValueExpr;

    public ColumnExpression getColumnExpr() {
        return columnExpr;
    }

    public void setColumnExpr(ColumnExpression columnExpr) {
        this.columnExpr = columnExpr;
    }

    public Expression getUpdateValueExpr() {
        return updateValueExpr;
    }

    public void setUpdateValueExpr(Expression updateValueExpr) {
        this.updateValueExpr = updateValueExpr;
    }

    @Override
    public ExpressionType getType() {
        return ET_SET_CLAUSE_EXPRESSION;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendExpression(columnExpr);
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(COT_EQUALS.getOperator());
        sqlBuilder.appendExpression(updateValueExpr);
    }
}
