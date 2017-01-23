package com.reforms.sql.expr.term;

import static com.reforms.sql.expr.term.ExpressionType.ET_COLUMN_EXPRESSION;

import com.reforms.sql.expr.viewer.SqlBuilder;

/**
 *
 * @author palihov
 */
public class ColumnExpression extends SelectableExpression {

    private String prefix;

    private String columnName;

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public boolean isAsterisk() {
        return "*".equals(columnName);
    }

    @Override
    public ExpressionType getType() {
        return ET_COLUMN_EXPRESSION;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendSpace();
        if (prefix != null) {
            sqlBuilder.append(prefix).append(".");
        }
        sqlBuilder.append(columnName);
    }
}
