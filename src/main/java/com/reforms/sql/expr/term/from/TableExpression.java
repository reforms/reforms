package com.reforms.sql.expr.term.from;

import static com.reforms.sql.expr.term.ExpressionType.ET_TABLE_EXPRESSION;

import com.reforms.sql.expr.term.AsClauseExpression;
import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.viewer.SqlBuilder;

/**
 *
 * @author palihov
 */
public class TableExpression extends TableReferenceExpression {

    private String schemaName;

    private String tableName;

    private AsClauseExpression asClauseExpr;

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public AsClauseExpression getAsClauseExpr() {
        return asClauseExpr;
    }

    public void setAsClauseExpr(AsClauseExpression asClauseExpr) {
        this.asClauseExpr = asClauseExpr;
    }

    @Override
    public ExpressionType getType() {
        return ET_TABLE_EXPRESSION;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendSpace();
        if (schemaName != null) {
            sqlBuilder.append(schemaName).append(".");
        }
        sqlBuilder.appendWord(tableName);
        sqlBuilder.appendExpression(asClauseExpr);
    }
}