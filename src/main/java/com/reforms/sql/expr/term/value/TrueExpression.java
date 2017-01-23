package com.reforms.sql.expr.term.value;

import static com.reforms.sql.expr.term.SqlWords.SW_TRUE;

public class TrueExpression extends ValueExpression {

    public TrueExpression() {
        super(SW_TRUE, ValueExpressionType.VET_TRUE);
    }

}