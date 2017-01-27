package com.reforms.sql.expr.statement;

import static com.reforms.sql.expr.term.ExpressionType.ET_PAGE_EXPRESSION;

import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.term.page.LimitExpression;
import com.reforms.sql.expr.term.page.OffsetExpression;
import com.reforms.sql.expr.viewer.SqlBuilder;

/**
 * Example: 'LIMIT 10 OFFSET 20'
 * @author evgenie
 */
public class PageStatement extends Expression {

    private LimitExpression limitExpr;

    private OffsetExpression offsetExpr;

    public LimitExpression getLimitExpr() {
        return limitExpr;
    }

    public void setLimitExpr(LimitExpression limitExpr) {
        this.limitExpr = limitExpr;
    }

    public OffsetExpression getOffsetExpr() {
        return offsetExpr;
    }

    public void setOffsetExpr(OffsetExpression offsetExpr) {
        this.offsetExpr = offsetExpr;
    }

    @Override
    public ExpressionType getType() {
        return ET_PAGE_EXPRESSION;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendExpression(limitExpr);
        sqlBuilder.appendExpression(offsetExpr);
    }

}
