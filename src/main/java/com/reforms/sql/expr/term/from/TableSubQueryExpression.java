package com.reforms.sql.expr.term.from;

import static com.reforms.sql.expr.term.ExpressionType.ET_TABLE_SUB_QUERY_EXPRESSION;

import com.reforms.sql.expr.query.SelectQuery;
import com.reforms.sql.expr.term.AsClauseExpression;
import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.viewer.SqlBuilder;

/**
 *
 * @author evgenie
 */
public class TableSubQueryExpression extends TableReferenceExpression {

    private SelectQuery subQueryExpr;

    private AsClauseExpression asClauseExpr;

    public SelectQuery getSubQueryExpr() {
        return subQueryExpr;
    }

    public void setSubQueryExpr(SelectQuery subQueryExpr) {
        this.subQueryExpr = subQueryExpr;
    }

    public AsClauseExpression getAsClauseExpr() {
        return asClauseExpr;
    }

    public void setAsClauseExpr(AsClauseExpression asClauseExpr) {
        this.asClauseExpr = asClauseExpr;
    }

    @Override
    public ExpressionType getType() {
        return ET_TABLE_SUB_QUERY_EXPRESSION;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendExpression(subQueryExpr);
        sqlBuilder.appendExpression(asClauseExpr);
    }
}
