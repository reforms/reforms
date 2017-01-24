package com.reforms.sql.expr.term.from;

import com.reforms.sql.expr.term.AsClauseExpression;
import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.viewer.SqlBuilder;

import static com.reforms.sql.expr.term.ExpressionType.ET_TABLE_EXPRESSION;

/**
 *
 * @author evgenie
 */
public class TableExpression extends TableReferenceExpression {

    private String schemeName;

    private String tableName;

    private AsClauseExpression asClauseExpr;

    public String getSchemeName() {
        return schemeName;
    }

    public boolean hasSchemeName() {
        return schemeName != null;
    }

    public void setSchemeName(String schemeName) {
        this.schemeName = schemeName;
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
        if (schemeName != null) {
            sqlBuilder.append(schemeName).append(".");
        }
        sqlBuilder.appendWord(tableName);
        sqlBuilder.appendExpression(asClauseExpr);
    }
}