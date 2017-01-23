package com.reforms.sql.expr.term.value;

import static com.reforms.sql.expr.term.SqlWords.SW_FALSE;

public class FalseExpression extends ValueExpression {

    public FalseExpression() {
        super(SW_FALSE, ValueExpressionType.VET_FALSE);
    }

}