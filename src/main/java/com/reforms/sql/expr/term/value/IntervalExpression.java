package com.reforms.sql.expr.term.value;

import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.viewer.SqlBuilder;

import static com.reforms.sql.parser.SqlWords.SW_INTERVAL;

/**
 * INTERVAL '1 day'
 * @author evgenie
 */
public class IntervalExpression extends ValueExpression {

    private final String intervalWord;

    public IntervalExpression(String dateTimeValue) {
        this(SW_INTERVAL, dateTimeValue);
    }

    public IntervalExpression(String timeWord, String dateTimeValue) {
        super(dateTimeValue, ValueExpressionType.VET_INTERVAL);
        intervalWord = timeWord;
    }

    @Override
    public ExpressionType getType() {
        return super.getType();
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(intervalWord);
        super.view(sqlBuilder);
    }
}