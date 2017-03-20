package com.reforms.sql.expr.term.value;

import com.reforms.sql.expr.term.ExpressionType;
import com.reforms.sql.expr.viewer.SqlBuilder;

import static com.reforms.sql.parser.SqlWords.SW_DATE;

/**
 * DATE '2017-03-13 11:54:55'
 * {d '2017-03-13 11:54:55'}
 * @author evgenie
 */
public class DateExpression extends ValueExpression {

    /** DATE or D */
    private final String dateWord;

    /** Short style with {d 'date'} */
    private final boolean jdbcFormat;

    public DateExpression(String dateTimeValue) {
        this(SW_DATE, dateTimeValue, false);
    }

    public DateExpression(String dateWord, String dateTimeValue, boolean jdbcFormat) {
        super(dateTimeValue, ValueExpressionType.VET_DATE);
        this.dateWord = dateWord;
        this.jdbcFormat = jdbcFormat;
    }

    @Override
    public ExpressionType getType() {
        return super.getType();
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        if (jdbcFormat) {
            sqlBuilder.appendSpace();
            sqlBuilder.append("{");
            sqlBuilder.appendWord(dateWord);
            super.view(sqlBuilder);
            sqlBuilder.append("}");
        } else {
            sqlBuilder.appendSpace();
            sqlBuilder.appendWord(dateWord);
            super.view(sqlBuilder);
        }
    }
}