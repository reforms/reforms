package com.reforms.sql.expr.term;

import static com.reforms.sql.expr.term.ExpressionType.ET_ASTERISK_EXPRESSION;

import com.reforms.sql.expr.viewer.SqlBuilder;

public class AsteriskExpression extends SelectableExpression {

    @Override
    public ExpressionType getType() {
        return ET_ASTERISK_EXPRESSION;
    }

    @Override
    public final void view(SqlBuilder sqlBuilder) {
        sqlBuilder.appendSpace();
        sqlBuilder.appendWord("*");
    }
}