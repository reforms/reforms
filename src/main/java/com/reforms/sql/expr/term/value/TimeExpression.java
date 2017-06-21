package com.reforms.sql.expr.term.value;

import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.viewer.SqlBuilder;

import static com.reforms.sql.parser.SqlWords.SW_TIME;

/**
 * TIME '2017-03-13 11:54:55'
 * {T '2017-03-13 11:54:55'}
 * @author evgenie
 */
public class TimeExpression extends ValueExpression {

    /** TIME or T */
    private final String timeWord;

    /** Short style with {t 'time'} */
    private final boolean jdbcFormat;

    public TimeExpression(String dateTimeValue) {
        this(SW_TIME, dateTimeValue, false);
    }

    public TimeExpression(String timeWord, String dateTimeValue, boolean jdbcFormat) {
        super(dateTimeValue, ValueExpressionType.VET_TIME);
        this.timeWord = timeWord;
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
            sqlBuilder.appendWord(timeWord);
            sqlBuilder.appendSpace();
            super.view(sqlBuilder);
            sqlBuilder.append("}");
        } else {
            sqlBuilder.appendWord(timeWord);
            sqlBuilder.appendSpace();
            super.view(sqlBuilder);
        }
    }
}