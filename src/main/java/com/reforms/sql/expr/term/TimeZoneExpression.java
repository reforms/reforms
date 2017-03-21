package com.reforms.sql.expr.term;

import com.reforms.sql.expr.viewer.SqlBuilder;

import static com.reforms.sql.expr.term.ExpressionType.ET_TIME_ZONE_EXPRESSION;
import static com.reforms.sql.parser.SqlWords.SW_AT_TIME_ZONE;

/**
 * AT TIME ZONE 'UTC'
 * @author evgenie
 */
public class TimeZoneExpression extends Expression {

    /** AT TIME ZONE */
    private String atTimeZonePhrase = SW_AT_TIME_ZONE;

    /** 'UTC' for example */
    private Expression timeZoneNameExpr;

    public String getAtTimeZonePhrase() {
        return atTimeZonePhrase;
    }

    public void setAtTimeZonePhrase(String atTimeZonePhrase) {
        this.atTimeZonePhrase = atTimeZonePhrase;
    }

    public Expression getTimeZoneNameExpr() {
        return timeZoneNameExpr;
    }

    public void setTimeZoneNameExpr(Expression timeZoneNameExpr) {
        this.timeZoneNameExpr = timeZoneNameExpr;
    }

    @Override
    public ExpressionType getType() {
        return ET_TIME_ZONE_EXPRESSION;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(atTimeZonePhrase);
        sqlBuilder.appendExpression(timeZoneNameExpr);
    }
}
