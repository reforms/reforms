package com.reforms.sql.expr.term;

import com.reforms.sql.expr.viewer.SqlBuilder;

import static com.reforms.sql.expr.term.ExpressionType.ET_EXTENDS_SELECTABLE_EXPRESSION;

/**
 * Расширяет любое выражение следюущими выражениями:
 *  1. AT TIME ZONE
 *  2. ::TYPE_CAST
 *  3. AS clause
 * @author evgenie
 */
public class ExtendsSelectableExpression extends SelectableExpression {

    private Expression primaryExpr;

    private TimeZoneExpression timeZoneExpr;

    private TypeCastExpression typeCastExpr;

    private AsClauseExpression asClauseExpr;

    public TimeZoneExpression getTimeZoneExpr() {
        return timeZoneExpr;
    }

    public void setTimeZoneExpr(TimeZoneExpression timeZoneExpr) {
        this.timeZoneExpr = timeZoneExpr;
    }

    public TypeCastExpression getTypeCastExpr() {
        return typeCastExpr;
    }

    public void setTypeCastExpr(TypeCastExpression typeCastExpr) {
        this.typeCastExpr = typeCastExpr;
    }

    public Expression getPrimaryExpr() {
        return primaryExpr;
    }

    public void setPrimaryExpr(Expression primaryExpr) {
        this.primaryExpr = primaryExpr;
    }

    public AsClauseExpression getAsClauseExpr() {
        return asClauseExpr;
    }

    public void setAsClauseExpr(AsClauseExpression asClauseExpr) {
        this.asClauseExpr = asClauseExpr;
    }

    @Override
    public ExpressionType getType() {
        return ET_EXTENDS_SELECTABLE_EXPRESSION;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendExpression(primaryExpr);
        sqlBuilder.appendExpression(timeZoneExpr);
        sqlBuilder.appendExpression(typeCastExpr);
        sqlBuilder.appendExpression(asClauseExpr);
    }
}
