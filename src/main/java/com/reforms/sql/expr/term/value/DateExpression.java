package com.reforms.sql.expr.term.value;

import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.viewer.SqlBuilder;

import static com.reforms.sql.parser.SqlWords.SW_DATE;

/**
 * DATE '2017-03-13 11:54:55'
 * @author evgenie
 */
public class DateExpression extends ValueExpression {

    private final String dateWord;

    public DateExpression(String dateTimeValue) {
        this(SW_DATE, dateTimeValue);
    }

    public DateExpression(String timeWord, String dateTimeValue) {
        super(dateTimeValue, ValueExpressionType.VET_DATE);
        dateWord = timeWord;
    }

    @Override
    public ExpressionType getType() {
        return super.getType();
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord(dateWord);
        super.view(sqlBuilder);
    }
}