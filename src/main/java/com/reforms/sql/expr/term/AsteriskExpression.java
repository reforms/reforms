package com.reforms.sql.expr.term;

import com.reforms.sql.expr.viewer.SqlBuilder;

import static com.reforms.sql.expr.term.ExpressionType.ET_ASTERISK_EXPRESSION;

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