package com.reforms.sql.expr.term;

import static com.reforms.sql.expr.term.ExpressionType.ET_GROUPING_COLUMN_REFERENCE_EXPRESSION;

import com.reforms.sql.expr.viewer.SqlBuilder;

public class GroupingColumnReferenceExpression extends Expression {

    private Expression columnRefExpr;

    public Expression getColumnRefExpr() {
        return columnRefExpr;
    }

    public void setColumnRefExpr(Expression columnRefExpr) {
        this.columnRefExpr = columnRefExpr;
    }

    @Override
    public ExpressionType getType() {
        return ET_GROUPING_COLUMN_REFERENCE_EXPRESSION;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendExpression(columnRefExpr);
    }
}
