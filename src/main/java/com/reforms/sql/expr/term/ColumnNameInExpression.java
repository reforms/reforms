package com.reforms.sql.expr.term;

import com.reforms.sql.expr.viewer.SqlBuilder;

import static com.reforms.sql.expr.term.ExpressionType.ET_COLUMN_NAME_IN_EXPRESSION;
import static com.reforms.sql.parser.SqlWords.SW_AS;

public class ColumnNameInExpression extends Expression {

    private Expression columnNameExpr;

    private String asWord = SW_AS;

    private SelectableExpression valueExpr;

    @Override
    public ExpressionType getType() {
        return ET_COLUMN_NAME_IN_EXPRESSION;
    }

    public Expression getColumnNameExpr() {
        return columnNameExpr;
    }

    public void setColumnNameExpr(Expression columnNameExpr) {
        this.columnNameExpr = columnNameExpr;
    }

    public SelectableExpression getValueExpr() {
        return valueExpr;
    }

    public boolean hasAsValue() {
        return valueExpr != null;
    }

    public void setValueExpr(SelectableExpression valueExpr) {
        this.valueExpr = valueExpr;
    }

    public String getAsWord() {
        return asWord;
    }

    public void setAsWord(String asWord) {
        this.asWord = asWord;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.append(columnNameExpr);
        if (valueExpr != null) {
            sqlBuilder.appendSpace();
            sqlBuilder.append(asWord);
            sqlBuilder.appendSpace();
            sqlBuilder.append(valueExpr);
        }
    }
}
