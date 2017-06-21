package com.reforms.sql.expr.term.value;

import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.viewer.SqlBuilder;

import static com.reforms.sql.parser.SqlWords.SW_TIMESTAMP;

/**
 * TIMESTAMP '2017-03-13 11:54:55'
 * {TS '2017-03-13 11:54:55'}
 * @author evgenie
 */
public class TimestampExpression extends ValueExpression {

    /** TIMESTAMP or TS */
    private final String timestampWord;

    /** Short style with {ts 'timestamp'} */
    private final boolean jdbcFormat;

    public TimestampExpression(String dateTimeValue) {
        this(SW_TIMESTAMP, dateTimeValue, false);
    }

    public TimestampExpression(String timestampWord, String dateTimeValue, boolean jdbcFormat) {
        super(dateTimeValue, ValueExpressionType.VET_TIMESTAMP);
        this.timestampWord = timestampWord;
        this.jdbcFormat = jdbcFormat;
    }

    @Override
    public ExpressionType getType() {
        return super.getType();
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        if (jdbcFormat) {
            sqlBuilder.append("{");
            sqlBuilder.appendWord(timestampWord);
            sqlBuilder.appendSpace();
            super.view(sqlBuilder);
            sqlBuilder.append("}");
        } else {
            sqlBuilder.appendWord(timestampWord);
            sqlBuilder.appendSpace();
            super.view(sqlBuilder);
        }
    }
}