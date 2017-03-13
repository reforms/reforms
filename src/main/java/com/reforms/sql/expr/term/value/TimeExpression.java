package com.reforms.sql.expr.term.value;

import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.viewer.SqlBuilder;

import static com.reforms.sql.parser.SqlWords.SW_TIME;

/**
 * TIME '2017-03-13 11:54:55'
 * @author evgenie
 */
public class TimeExpression extends ValueExpression {

    private final String timeWord;

    public TimeExpression(String dateTimeValue) {
        this(SW_TIME, dateTimeValue);
    }

    public TimeExpression(String timeWord, String dateTimeValue) {
        super(dateTimeValue, ValueExpressionType.VET_TIME);
        this.timeWord = timeWord;
    }

    @Override
    public ExpressionType getType() {
        return super.getType();
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(timeWord);
        super.view(sqlBuilder);
    }
}