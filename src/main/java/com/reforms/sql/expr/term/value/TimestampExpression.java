package com.reforms.sql.expr.term.value;

import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.viewer.SqlBuilder;

import static com.reforms.sql.parser.SqlWords.SW_TIMESTAMP;

/**
 * TIMESTAMP '2017-03-13 11:54:55'
 * @author evgenie
 */
public class TimestampExpression extends ValueExpression {

    private final String timestampWord;

    public TimestampExpression(String dateTimeValue) {
        this(SW_TIMESTAMP, dateTimeValue);
    }

    public TimestampExpression(String timeWord, String dateTimeValue) {
        super(dateTimeValue, ValueExpressionType.VET_TIMESTAMP);
        timestampWord = timeWord;
    }

    @Override
    public ExpressionType getType() {
        return super.getType();
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(timestampWord);
        super.view(sqlBuilder);
    }
}