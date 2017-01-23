package com.reforms.sql.expr.term.value;

import static com.reforms.sql.expr.term.SqlWords.SW_NULL;

public class NullExpression extends ValueExpression {

    public NullExpression() {
        super(SW_NULL, ValueExpressionType.VET_NULL);
    }

}